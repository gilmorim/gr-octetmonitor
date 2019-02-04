import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import jnr.ffi.annotations.In;
import org.snmp4j.*;
import org.snmp4j.agent.*;
import org.snmp4j.agent.example.SampleAgent;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.transport.TransportMappings;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static org.snmp4j.agent.mo.MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE;
import static org.snmp4j.agent.mo.ext.AgentppSimulationMib.AgentppSimModeEnum.config;

/**
 * This Agent contains mimimal functionality for running a version 2c snmp
 * agent.
 * 
 * 
 * @author johanrask
 * 
 */
public class Agent extends BaseAgent implements MOChangeListener {

	// not needed but very useful of course
	static {
		LogFactory.setLogFactory(new Log4jLogFactory());
	}
	private Snmp snmp;
	private String address;
	private UserTarget target;

	public Agent(String address) throws IOException {

		/**
		 * Creates a base agent with boot-counter, config file, and a
		 * CommandProcessor for processing SNMP requests. Parameters:
		 * "bootCounterFile" - a file with serialized boot-counter information
		 * (read/write). If the file does not exist it is created on shutdown of
		 * the agent. "configFile" - a file with serialized configuration
		 * information (read/write). If the file does not exist it is created on
		 * shutdown of the agent. "commandProcessor" - the CommandProcessor
		 * instance that handles the SNMP requests.
		 */
		// These files does not exist and are not used but has to be specified
		// Read snmp4j docs for more info
		super(new File("conf.agent"), new File("bootCounter.agent"),
				new CommandProcessor(
						new OctetString(MPv3.createLocalEngineID())));
		this.address = address;
	}

	/**
	 * We let clients of this agent register the MO they
	 * need so this method does nothing
	 */


	/**
	 * Clients can register the MO they need
	 */
	public void registerManagedObject(ManagedObject mo) {
		try {
			server.register(mo, null);
			System.out.print("Successfully registered ");
		} catch (DuplicateRegistrationException e) {
			System.out.print("Failed to register ");
		}
	}

	public void unregisterManagedObject(MOGroup moGroup) {
		moGroup.unregisterMOs(server, getContext(moGroup));
	}

	@Override
	protected void unregisterManagedObjects() {
		// here we should unregister those objects previously registered...
	}

	/*
	 * Empty implementation
	 */
	@Override
	protected void addNotificationTargets(SnmpTargetMIB targetMIB,
										  SnmpNotificationMIB notificationMIB) {
	}

	/**
	 * Minimal View based Access Control
	 * <p>
	 * http://www.faqs.org/rfcs/rfc2575.html
	 */
	@Override
	protected void addViews(VacmMIB vacm) {
		UniversalVariables UV = UniversalVariables.getInstance();
		String com = UV.Get_CMS();
		//security model + securityname, group name + storagetype
		vacm.addGroup(SecurityModel.SECURITY_MODEL_ANY, new OctetString(com), new OctetString("com2sec"), StorageType.nonVolatile);
		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(com), new OctetString("com2sec"), StorageType.other);
		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(com), new OctetString("com2sec"), StorageType.volatile_);
		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(com), new OctetString("com2sec"), StorageType.nonVolatile);
		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(com), new OctetString("com2sec"), StorageType.permanent);
		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(com), new OctetString("com2sec"), StorageType.readOnly);
		//group name + context prefix + security model + security level  + match + readview + read vview +write view + notify view + storage type
		vacm.addAccess(new OctetString("com2sec"), new OctetString(com), SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString(com), new OctetString(com), new OctetString(com), StorageType.nonVolatile);
		// view name + subtree + mask + type + storagetype
		vacm.addViewTreeFamily(new OctetString(com), new OID("1.3.6.1.3.2019"), new OctetString(com), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString(com), new OID("1.3.6.1.3.2019.3"), new OctetString(com), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString(com), new OID("1.3.6.1.3.2019.3.1."), new OctetString(com), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString(com), new OID("1.3.6.1.3.2019.3.1.4"), new OctetString(com), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString(com), new OID("1.3.6.1.3.2019.3.1.4.0"), new OctetString(com), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString(com), new OID("1.3.6.1.3.2019.3.1.4.1"), new OctetString(com), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString(com), new OID("1.3.6.1.3.2019.3.1.4.2"), new OctetString(com), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);

	}
	protected void addCommunities(SnmpCommunityMIB communityMIB) {
		UniversalVariables UV = UniversalVariables.getInstance();
		String com = UV.Get_CMS();
		Variable[] com2sec = new Variable[]{
				new OctetString(com), // community name
				new OctetString(com), // security name
				getAgent().getContextEngineID(), // local engine ID
				new OctetString(com), // default context name
				new OctetString(com), // transport tag
				new Integer32(StorageType.nonVolatile), // storage type
				new Integer32(RowStatus.active) // row status
		};
		MOTableRow row = communityMIB.getSnmpCommunityEntry().createRow(new OctetString(com).toSubIndex(true), com2sec);
		communityMIB.getSnmpCommunityEntry().addRow(row);
	}

	/**
	 * User based Security Model, only applicable to
	 * SNMP v.3
	 */
	protected void addUsmUser(USM usm) {
	}

	protected void initTransportMappings() throws IOException {
		transportMappings = new TransportMapping[1];
		//Address addr = GenericAddress.parse(address);
		//TransportMapping tm = TransportMappings.getInstance().createTransportMapping(addr);
		transportMappings[0] = TransportMappings.getInstance().createTransportMapping(GenericAddress.parse(address));
	}

	/**
	 * Start method invokes some initialization methods needed to
	 * start the agent
	 *
	 * @throws IOException
	 */
	public void start() throws IOException {

		init();
		// This method reads some old config from a file and causes
		// unexpected behavior.
		// loadConfig(ImportModes.REPLACE_CREATE);
		addShutdownHook();
		UniversalVariables UV = UniversalVariables.getInstance();
		String com = UV.Get_CMS();
		getServer().addContext(new OctetString(com));
		finishInit();
		run();
		sendColdStartNotification();
	}


	@Override
	protected void registerManagedObjects( ) {

		getSnmpv2MIB().unregisterMOs(server, getContext(getSnmpv2MIB()));
		UniversalVariables UV = UniversalVariables.getInstance();
		SingleParam SP = SingleParam.getInstance();
		String indexp = SP.Get_Indexp();
		String imagep = SP.Get_indImagep();
		String flagp = SP.Get_flagp();
		MOScalar ms = new MOScalar(new OID("1.3.6.1.3.2019.1.1.0"), MOAccessImpl.ACCESS_READ_WRITE, new Integer32(Integer.parseInt(indexp)));
		MOScalar mc = new MOScalar(new OID("1.3.6.1.3.2019.1.2.0"), MOAccessImpl.ACCESS_READ_WRITE, new Integer32(Integer.parseInt(imagep)));
		MOScalar mv = new MOScalar(new OID("1.3.6.1.3.2019.1.3.0"), MOAccessImpl.ACCESS_READ_WRITE, new Integer32(Integer.parseInt(flagp)));
		registerManagedObject(ms);
		registerManagedObject(mc);
		registerManagedObject(mv);
		ms.addMOChangeListener(this);
		mc.addMOChangeListener(this);
		mv.addMOChangeListener(this);
		UV.Put_escalar_param_1(ms);
		UV.Put_escalar_param_2(mc);
		//Table of imagens
		MOAccess Permissao = new MOAccessImpl(ACCESSIBLE_FOR_READ_WRITE);
		SingleTableImage TI = SingleTableImage.getInstance();
		int size = TI.Get_size();
			for (int j=0; j <size; j++) {
				//registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.2.1.1."+oid+".0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(String.valueOf(j))));
				MOTableBuilder builder = new MOTableBuilder(new OID("1.3.6.1.3.2019.2.1.")).addColumnType(SMIConstants.SYNTAX_INTEGER,  Permissao);
				builder.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE);
				for (int k = 0; k < size; k++) {
					String id = TI.Get_ID_by_inteiroseq(k);
					builder.addRowValue(new Integer32(k));
					String indImage = TI.Get_Image_by_id(String.valueOf(k));
					builder.addRowValue(new OctetString(indImage));
				}
				int[] indexes = new int[size];
				for (int k = 0; k < size; k++) {
					String id = TI.Get_ID_by_inteiroseq(k);
					indexes[k]=k;
				}
				registerManagedObject(builder.build(indexes));
			}

		//Table container
		SingleCointainerTable C = SingleCointainerTable.getInstance();
		int sizec = C.Get_size();
		for (int j=0; j <sizec; j++) {
			//registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.2.1.1."+oid+".0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(String.valueOf(j))));
			MOTableBuilder builder = new MOTableBuilder(new OID("1.3.6.1.3.2019.3.1."))
					.addColumnType(SMIConstants.SYNTAX_INTEGER, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_INTEGER, MOAccessImpl.ACCESS_READ_WRITE);

				String id = C.Get_Index_by_the_ID(j);
				builder.addRowValue(new Integer32(Integer.parseInt(id)));
				String namec = C.Get_Name_by_ID(id);
				builder.addRowValue(new OctetString(namec));
				String imagec = C.Get_Image_by_ID(id);
				builder.addRowValue(new OctetString(imagec));
				String statusc = C.Get_Status_by_ID(id);
				builder.addRowValue(new OctetString(statusc));
				//String processorc = C.Get_Processor_by_ID(id);
				builder.addRowValue(new Integer32(Integer.parseInt("2")));

			int[] indexes = new int[sizec];
			for (int k = 0; k < sizec; k++) {
				String idx = C.Get_Index_by_the_ID(k);
				indexes[k]=Integer.parseInt(idx);
			}
			registerManagedObject(builder.build(indexes));
		}
		//status

		SingleStatus S = SingleStatus.getInstance();

			for (int k = 0; k < 1; k++) {
			String indexs = S.Get_ID_by_inteiro(0);
			String userids = S.Get_userIds_by_id(String.valueOf(k));
			String timesticksinit = S.Get_Timebegins_by_id(String.valueOf(k));
			TimeTicks timeinit = new TimeTicks(Long.parseLong(timesticksinit));
			String timesticksfinal = S.Get_Timefinals_by_id(String.valueOf(k));
			TimeTicks timefinal = new TimeTicks(Long.parseLong(timesticksfinal));
			int counter = S.Get_counter_by_id(String.valueOf(k));
			//int counter_int = Integer.valueOf(counter);
			registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.4.1.0"), MOAccessImpl.ACCESS_READ_WRITE, new Integer32(Integer.parseInt(indexs))));
			registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.4.3.0"), MOAccessImpl.ACCESS_READ_WRITE, new TimeTicks(timeinit)));
			registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.4.4.0"), MOAccessImpl.ACCESS_READ_WRITE, new TimeTicks(timefinal)));
			registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.4.5.0"), MOAccessImpl.ACCESS_READ_WRITE, new Counter64(counter)));
			}


	}

	/**
	 * Adds community to security name mappings needed for SNMPv1 and SNMPv2c.
	 */
	public ResponseEvent snmpSetOperation(VariableBinding[] vars)
			throws IOException {
		System.out.println("entrou");

		PDU setPdu = new ScopedPDU();
		for (VariableBinding variableBinding : vars) {
			setPdu.add(variableBinding);
		}
		return snmp.send(setPdu, target);
	}

	public static void main(String[] args) throws IOException, InterruptedException, DockerCertificateException, DockerException, URISyntaxException {


		String configuration = args[0];
		String images = args[1];
		System.out.println(configuration);
		System.out.println(images);
		List<String> list = new ArrayList<String>();
		//abrir o primeiro ficheiro de configuraçao para tira a porta e CMS
		File file = new File(configuration);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			while ((text = reader.readLine()) != null) {
				list.add(text);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		//1.3.6.1.3.2019.1.1.
		//print out the list
		String udp_port = list.get(0);
		//Parse porta
		String[] config = udp_port.split(Pattern.quote(" "));
		String porta = config[1];
		System.out.println(porta);
		//parse da community string
		String cms = list.get(1);
		String[] community_s = cms.split(Pattern.quote(" "));
		String community_string = community_s[1];
		//abrir singleton
		UniversalVariables UV = UniversalVariables.getInstance();
		UV.Put_CMS(community_string);
		System.out.println(community_string);
		initparam();
		initTableImage(images);
		initContainerTable();
		initTableStatus();


		Agent agent = new Agent("127.0.0.1/" + porta);
		agent.start();
		TransportMapping transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();
		ResponseListener listener = new ResponseListener() {
			@Override
			public void onResponse(ResponseEvent responseEvent) {
				((Snmp)responseEvent.getSource()).cancel(responseEvent.getRequest(),this);
				PDU response =responseEvent.getResponse();
				System.out.println(response);
			}
		};
		//MOChangeListener Mo
		/*
		Variable[] vars = new Variable[]{
				new OctetString(), // community name
		}
			ResponseEvent response = agent
					.snmpSetOperation((VariableBinding[]) vars);
		System.out.println(response.getResponse());*/
		agent.unregisterManagedObject(agent.getSnmpv2MIB());
		while (true) {
			System.out.println("Agent running...");
			Thread.sleep(5000);
		}
	}


	public static void initparam() throws DockerCertificateException, DockerException, InterruptedException, IOException, URISyntaxException {

		//DockerInformation DI = new DockerInformation();
		//DI.createcontainer("postgres");

		List<String> lista_de_param_file = new ArrayList<String>();
		File file = new File("resultados.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			while ((text = reader.readLine()) != null) {
				lista_de_param_file.add(text);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		//get do file indexp
		System.out.println("o que recebe da lista de parametros :" + lista_de_param_file);
		String indexp_file = lista_de_param_file.get(0);
		String[] indexp_file_split = indexp_file.split(Pattern.quote("|"));
		String indexp_value = indexp_file_split[2];
		System.out.println(indexp_value);
		//get do file indice da imagem
		String indImage_file = lista_de_param_file.get(1);
		String[] indImage_file_split = indImage_file.split(Pattern.quote("|"));
		String indImage_value = indImage_file_split[2];
		System.out.println(indImage_value);
		//get do file flag
		String flagp_file = lista_de_param_file.get(2);
		String[] flagp_file_split = flagp_file.split(Pattern.quote("|"));
		String flagp_value = flagp_file_split[2];
		System.out.println(flagp_value);
		//colocar no singleton
		SingleParam SP = SingleParam.getInstance();
		SP.Put_Indexp(indexp_value);
		SP.Put_indImagep(indImage_value);
		SP.Put_flagp(flagp_value);

	}

	public static void initTableImage(String images) throws DockerException, InterruptedException, DockerCertificateException {
        List<String> list = new ArrayList<String>();
        File file = new File(images);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
                list.add(text);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        DockerInformation DI = new DockerInformation();
        TreeMap statuses= DI.getname();

        for(int i = 0;i<list.size();i++){
            SingleTableImage TI = SingleTableImage.getInstance();
            TI.Put_ID_Image(String.valueOf(i),String.valueOf(list.get(i)));
            TI.Put_size(list.size());
        }

       // TI.Put_ID_Image(String.valueOf(i),info.config().image());
        //TI.Put_size(allContainers.size());
	}

	public static void initContainerTable() throws DockerCertificateException, DockerException, InterruptedException {

		int counter =0;
		DockerInformation DI = new DockerInformation();
		TreeMap name = DI.getname();
		TreeMap image = DI.getImage();
		TreeMap status = DI.getContainersStatuses();
		TreeMap processor = DI.getContainersProcessor();
		SingleCointainerTable C = SingleCointainerTable.getInstance();
		for (int i=0; i<name.size();i++) {
            C.Put_ID_CointainerTable_Index(i, String.valueOf(i));
            C.Put_ID_CointainerTable_Name(String.valueOf(i),String.valueOf(name.get(i+1)));
            C.Put_ID_CointainerTable_Image(String.valueOf(i),String.valueOf(image.get(i+1)));
            C.Put_ID_CointainerTable_Status(String.valueOf(i),String.valueOf(status.get(i+1)));
            C.Put_ID_CointainerTable_Processor(String.valueOf(i), String.valueOf(processor.get(i+1)));
        }
		C.Put_size(name.size());

	}
	public static void initTableStatus(){

		int countador_de_users=0;
		int contador_Timestick_inicial =0;
		int contador_Timestick_final =0;
		//Ficheiro de configuração das imagens
		List<String> lista_de_tablestatus = new ArrayList<String>();
		File file = new File("resultados.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			while ((text = reader.readLine()) != null) {
				lista_de_tablestatus.add(text);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		SingleStatus S = SingleStatus.getInstance();

		for (int i = 0; i < lista_de_tablestatus.size(); i++) {
			String linhadoficheiro = lista_de_tablestatus.get(i);
			String[] oidporpontos = linhadoficheiro.split(Pattern.quote("."));
			String oid_obje = oidporpontos[6];
			String oid_insta = oidporpontos[7];
			if (oid_obje.equals("4")) {
				//se for userids
				if (oid_insta.equals("1")) {
					//buscar index
					String oid_index_bruto = oidporpontos[8];
					String[] oid_index_barras = oid_index_bruto.split(Pattern.quote("|"));
					String oid_index = oid_index_barras[0];
					//buscar valor do index
					String index_value = oid_index_barras[2];
					S.Put_Int_ID(0,index_value);
				}
				if (oid_insta.equals("2")) {
					//buscar index
					String oid_index_bruto = oidporpontos[8];
					String[] oid_index_barras = oid_index_bruto.split(Pattern.quote("|"));
					String oid_index = oid_index_barras[0];
					//buscar valor user
					String userids_value = oid_index_barras[2];
					S.Put_ID_userIds(oid_index,userids_value);
				}
				if (oid_insta.equals("3")) {
					//buscar index
					String oid_index_bruto = oidporpontos[8];
					String[] oid_index_barras = oid_index_bruto.split(Pattern.quote("|"));
					String oid_index = oid_index_barras[0];
					//buscar valor timestick inicial
					String timestickinicial = oid_index_barras[2];
					S.Put_ID_Timebegins(oid_index,timestickinicial);
				}
				if (oid_insta.equals("4")) {
					//buscar index
					String oid_index_bruto = oidporpontos[8];
					String[] oid_index_barras = oid_index_bruto.split(Pattern.quote("|"));
					String oid_index = oid_index_barras[0];
					//buscar valor timestick final
					String timestickfinal = oid_index_barras[2];
					S.Put_ID_Timefinals(oid_index,timestickfinal);

				}

				if (oid_insta.equals("5")) {
					//buscar index
					String oid_index_bruto = oidporpontos[8];
					String[] oid_index_barras = oid_index_bruto.split(Pattern.quote("|"));
					String oid_index = oid_index_barras[0];
					//buscar valor counter
					String counter_value = oid_index_barras[2];
					int counter_value_int = Integer.parseInt(counter_value);
					S.Put_ID_counter(oid_index,counter_value_int);

				}
		}

		}
	}

	@Override
	public void beforePrepareMOChange(MOChangeEvent moChangeEvent) {

	}

	@Override
	public void afterPrepareMOChange(MOChangeEvent moChangeEvent) {

	}

	@Override
	public void beforeMOChange(MOChangeEvent moChangeEvent) {

	}

	@Override
	public void afterMOChange(MOChangeEvent moChangeEvent) {
		moChangeEvent.getChangedObject();
		Variable smi = moChangeEvent.getNewValue();
		OID oc = 	moChangeEvent.getOID();
		Variable mc = moChangeEvent.getOldValue();
		System.out.println(smi);
		System.out.println(oc);
		String OID = oc.toString();
		String[] oidporpontos = OID.split(Pattern.quote("."));
		System.out.println(Arrays.toString(oidporpontos));
		String objecto = oidporpontos[6];
		String instancia = oidporpontos[7];
		String value = oidporpontos[8];
		SingleTableImage TI = SingleTableImage.getInstance();
		UniversalVariables UV = UniversalVariables.getInstance();
		if (objecto.equals("1")){
			if (instancia.equals("1")){
				String imagem = TI.Get_Image_by_id(value);
				if (value.equals("null")){
					System.out.println("Não existem imagens com esse id ");
				}
				else {
					MOScalar param2 = UV.Get_escalar_param_2();
					param2.setValue(new OctetString(imagem));

				}
			}
		}
	}
}

