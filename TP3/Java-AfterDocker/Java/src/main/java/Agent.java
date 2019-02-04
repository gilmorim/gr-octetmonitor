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
	int g =0;

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
		MOScalar mp1 = new MOScalar(new OID("1.3.6.1.3.2019.1.1.0"), MOAccessImpl.ACCESS_READ_WRITE, new Integer32(Integer.parseInt(indexp)));
		MOScalar mp2 = new MOScalar(new OID("1.3.6.1.3.2019.1.2.0"), MOAccessImpl.ACCESS_READ_WRITE, new OctetString(imagep));
		MOScalar mp3 = new MOScalar(new OID("1.3.6.1.3.2019.1.3.0"), MOAccessImpl.ACCESS_READ_WRITE, new Integer32(Integer.parseInt(flagp)));
		registerManagedObject(mp1);
		registerManagedObject(mp2);
		registerManagedObject(mp3);
		mp1.addMOChangeListener(this);
		mp2.addMOChangeListener(this);
		mp3.addMOChangeListener(this);
		UV.Put_escalar_param_1(mp1);
		UV.Put_escalar_param_2(mp2);
		UV.Put_escalar_param_3(mp3);
		SingleStatus S = SingleStatus.getInstance();
		//Status
		for (int k = 0; k < 1; k++) {
			String indexs = S.Get_ID_by_inteiro(0);
			String timesticksinit = S.Get_Timebegins_by_id(String.valueOf(k));
			TimeTicks timeinit = new TimeTicks(Long.parseLong(timesticksinit));
			int counter = S.Get_counter_by_id(String.valueOf(k));
			//int counter_int = Integer.valueOf(counter);
			registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.4.1.0"), MOAccessImpl.ACCESS_READ_WRITE, new Integer32(Integer.parseInt(indexs))));
			registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.4.2.0"), MOAccessImpl.ACCESS_READ_WRITE, new TimeTicks(timeinit)));
			registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.4.3.0"), MOAccessImpl.ACCESS_READ_WRITE, new Counter64(counter)));
		}

		//Table of imagens
		MOAccess Permissao = new MOAccessImpl(ACCESSIBLE_FOR_READ_WRITE);
		SingleTableImage TI = SingleTableImage.getInstance();
		int size = TI.Get_size();
			for (int j=0; j <size; j++) {
				//registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.2.1.1."+oid+".0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(String.valueOf(j))));
				MOTableBuilder builder = new MOTableBuilder(new OID("1.3.6.1.3.2019.2.1.")).addColumnType(SMIConstants.SYNTAX_INTEGER,  MOAccessImpl.ACCESS_READ_WRITE);
				builder.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE);
				for (int k = 0; k < size; k++) {
					String id = TI.Get_ID_by_inteiroseq(k);
					builder.addRowValue(new Integer32(k+1));
					String indImage = TI.Get_Image_by_id(String.valueOf(k));
					builder.addRowValue(new OctetString(indImage));
				}
				int[] indexes = new int[size];
				for (int k = 0; k < size; k++) {
					String id = TI.Get_ID_by_inteiroseq(k);
					indexes[k]=k+1;
				}
				registerManagedObject(builder.build(indexes));
			}
		//Table container

		SingleCointainerTable C = SingleCointainerTable.getInstance();
		int sizec = C.Get_size();
		if (sizec ==0 ){
			MOTableBuilder builder = new MOTableBuilder(new OID("1.3.6.1.3.2019.3.1."))
			.addColumnType(SMIConstants.SYNTAX_INTEGER, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
			.addColumnType(SMIConstants.SYNTAX_INTEGER, MOAccessImpl.ACCESS_READ_WRITE);

			builder.addRowValue(new Integer32(0));
			builder.addRowValue(new OctetString("null"));
			builder.addRowValue(new OctetString("null"));
			builder.addRowValue(new OctetString("null"));
			builder.addRowValue(new Integer32(0));

			int[] indexes = new int[1];
			for (int k = 0; k < 1; k++) {
				indexes[k]=k;
			}
			registerManagedObject(builder.build(indexes));
		}

		else {
			for (int j=0; j <sizec; j++) {
				//registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.2.1.1."+oid+".0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(String.valueOf(j))));
				MOTableBuilder builder = new MOTableBuilder(new OID("1.3.6.1.3.2019.3.1."))
						.addColumnType(SMIConstants.SYNTAX_INTEGER, MOAccessImpl.ACCESS_READ_WRITE)
						.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
						.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
						.addColumnType(SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_WRITE)
						.addColumnType(SMIConstants.SYNTAX_INTEGER, MOAccessImpl.ACCESS_READ_WRITE);
				for (int k = 0; k< sizec; k++){

					String id = C.Get_Index_by_the_ID(k);
					builder.addRowValue(new Integer32(Integer.parseInt(id)+1));
					String namec = C.Get_Name_by_ID(id);
					builder.addRowValue(new OctetString(namec));
					String imagec = C.Get_Image_by_ID(id);
					builder.addRowValue(new OctetString(imagec));
					String statusc = C.Get_Status_by_ID(id);
					builder.addRowValue(new OctetString(statusc));
					//String processorc = C.Get_Processor_by_ID(id);
					builder.addRowValue(new Integer32(Integer.parseInt("2")));
				}

				int[] indexes = new int[sizec];
				for (int k = 0; k < sizec; k++) {
					String idx = C.Get_Index_by_the_ID(k);
					indexes[k]=Integer.parseInt(idx)+1;
				}
				registerManagedObject(builder.build(indexes));
			}
		}


	}


	/**
	 * Adds community to security name mappings needed for SNMPv1 and SNMPv2c.
	 */

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
		UV.Put_porta(porta);
		System.out.println(community_string);

			initparam();
			initTableImage(images);
			initContainerTable();
			initTableStatus();

			criaragente();

	}

	public static void criaragente( ) throws IOException, InterruptedException {
		UniversalVariables UV = UniversalVariables.getInstance();
		String porta = UV.Get_porta();
		Agent agent = new Agent("127.0.0.1/" + porta);
		agent.start();
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
			Thread.sleep(100000);
		}
	}

	public static void initparam() throws DockerCertificateException, DockerException, InterruptedException, IOException, URISyntaxException {

		//DockerInformation DI = new DockerInformation();
		//DI.createcontainer("postgres");
		//colocar no singleton
		SingleParam SP = SingleParam.getInstance();
		SP.Put_Indexp("0");
		SP.Put_indImagep("0");
		SP.Put_flagp("0");

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

		SingleStatus S = SingleStatus.getInstance();

		for (int i = 0; i < 1; i++) {
					String index_value = "0";
					S.Put_Int_ID(0,index_value);
					String userids_value = "0";
					S.Put_ID_userIds(String.valueOf(i),userids_value);
					S.Put_ID_Timebegins(String.valueOf(i),"512832217");
					S.Put_ID_counter(String.valueOf(i),989261567);

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
		do {
			moChangeEvent.getChangedObject();
			Variable smi = moChangeEvent.getNewValue();
			OID oc = moChangeEvent.getOID();
			System.out.println(smi);
			System.out.println(oc);
			String OID = oc.toString();
			g = 1;


			String[] oidporpontos = OID.split(Pattern.quote("."));
			System.out.println(Arrays.toString(oidporpontos));
			String objecto = oidporpontos[6];
			String instancia = oidporpontos[7];
			String value = oidporpontos[8];
			moChangeEvent = null;
			SingleParam SP = SingleParam.getInstance();
			SingleTableImage TI = SingleTableImage.getInstance();
			UniversalVariables UV = UniversalVariables.getInstance();
			if (objecto.equals("1")) {
				if (instancia.equals("1")) {
					String imagem = TI.Get_Image_by_id(String.valueOf(smi));
					System.out.println(imagem);
					if (imagem == null) {
						System.out.println("Não existem imagens com esse id ");
						MOScalar param1 = UV.Get_escalar_param_1();
						MOScalar param2 = UV.Get_escalar_param_2();
						MOScalar param3 = UV.Get_escalar_param_3();
						param3.setValue(new Integer32(0));
						param2.setValue(new OctetString("ERRO"));
						param1.setValue(new Integer32(0));
					} else {
						SP.Put_id_snmpset(Integer.parseInt(String.valueOf(smi)));
						SP.Put_Indexp(value);
						SP.Put_indImagep(imagem);
						SP.Put_flagp("0");
						MOScalar param2 = UV.Get_escalar_param_2();
						MOScalar param3 = UV.Get_escalar_param_3();
						param3.setValue(new Integer32(0));
						param2.setValue(new OctetString(imagem));

					}
				}
				if (instancia.equals("2")) {
					String ID = TI.Get_ID_by_Image(String.valueOf(smi));
					SP.Put_id_snmpset(Integer.parseInt(ID));
					if (ID == null) {
						System.out.println("Não existem imagens com esse id ");
						MOScalar param1 = UV.Get_escalar_param_1();
						MOScalar param2 = UV.Get_escalar_param_2();
						MOScalar param3 = UV.Get_escalar_param_3();
						param3.setValue(new Integer32(0));
						param2.setValue(new OctetString("ERRO"));
						param1.setValue(new Integer32(0));
					} else {
						SP.Put_Indexp(value);
						SP.Put_indImagep(String.valueOf(smi));
						SP.Put_flagp("0");
						MOScalar param1 = UV.Get_escalar_param_1();
						MOScalar param2 = UV.Get_escalar_param_2();
						MOScalar param3 = UV.Get_escalar_param_3();
						param3.setValue(new Integer32(0));
						param1.setValue(new Integer32(Integer.valueOf(ID)
						));

					}
				}


				// set flag
				if (instancia.equals("3")) {
					String flag = SP.Get_flagp();
					if (flag == null) {
						System.out.println("Não existem imagens com esse id ");
						MOScalar param1 = UV.Get_escalar_param_1();
						MOScalar param2 = UV.Get_escalar_param_2();
						MOScalar param3 = UV.Get_escalar_param_3();
						param3.setValue(new Integer32(0));
						param2.setValue(new OctetString("ERRO"));
						param1.setValue(new Integer32(0));
					} else {

						int id_daimagem = SP.Get_id_snmpset_param();
						String imagem = TI.Get_Image_by_id(String.valueOf(id_daimagem));
						MOScalar param3 = UV.Get_escalar_param_3();
						param3.setValue(new Integer32(1));
						criarcontainer(imagem);
						//SingleStatus statistics = SingleStatus.getInstance();
						//String indexp = SP.Get_Indexp();
						//StringBuilder timestamp = new StringBuilder();
						//timestamp.append(System.currentTimeMillis());
						//statistics.Put_ID_Timebegins(indexp, timestamp.toString());
						//MOMutableTableModel mode = UV.Get_Table_3()
						instancia = null;
						oc = null;
					}



				}
				instancia = null;
				oc = null;
				OID = null;
			}

		}while (g==0);
	}
	public void criarcontainer (String imagem)  {


		DockerInformation DI = null;
		try {
			DI = new DockerInformation();
			DI.createcontainer(imagem);

		} catch (DockerCertificateException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (DockerException e) {
			e.printStackTrace();
		}


	}
}






