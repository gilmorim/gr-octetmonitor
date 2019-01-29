import org.ietf.jgss.Oid;

import org.snmp4j.TransportMapping;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.TransportMappings;
import org.snmp4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This Agent contains mimimal functionality for running a version 2c snmp
 * agent.
 * 
 * 
 * @author johanrask
 * 
 */
public class Agent extends BaseAgent {

	// not needed but very useful of course
	static {
		LogFactory.setLogFactory(new Log4jLogFactory());
	}

	private String address;

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

		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(
						"cpublic"), new OctetString("v1v2group"),
				StorageType.nonVolatile);

		vacm.addAccess(new OctetString("v1v2group"), new OctetString("public"),
				SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV,
				MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
				new OctetString("fullWriteView"), new OctetString(
						"fullNotifyView"), StorageType.nonVolatile);

		vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"),
				new OctetString(), VacmMIB.vacmViewIncluded,
				StorageType.nonVolatile);
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
		getServer().addContext(new OctetString("public"));
		finishInit();
		run();
		sendColdStartNotification();
	}


	@Override
	protected void registerManagedObjects() {

		getSnmpv2MIB().unregisterMOs(server, getContext(getSnmpv2MIB()));
		SingleParam SP = SingleParam.getInstance();
		String indexp = SP.Get_Indexp();
		String imagep = SP.Get_indImagep();
		String flagp = SP.Get_flagp();
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.1.1.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(indexp)));
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.1.2.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(imagep)));
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.1.3.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(flagp)));
		//Table of imagens
		SingleTableImage TI = SingleTableImage.getInstance();
		int size = TI.Get_size();
			for (int j=0; j <size; j++) {
				String oid = String.valueOf(j);
				//registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.2.1.1."+oid+".0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(String.valueOf(j))));

				MOTableBuilder builder = new MOTableBuilder(new OID("1.3.6.1.3.2019.2.1."))
						.addColumnType(SMIConstants.SYNTAX_INTEGER, MOAccessImpl.ACCESS_READ_ONLY);
				// Normally you would begin loop over you two domain objects her


					//next row
					builder.addColumnType(SMIConstants.SYNTAX_OCTET_STRING,MOAccessImpl.ACCESS_READ_ONLY);
					for (int k=0; k<size;k++) {
						builder.addRowValue(new Integer32(k));
						String indImage = TI.Get_Image_by_id(String.valueOf(k));
						builder.addRowValue(new OctetString(indImage));
					}
				registerManagedObject(builder.build());
		//object of container
		SingleCointainer C = SingleCointainer.getInstance();
		String indexc = C.Get_Indexc();
		String namec = C.Get_namec();
		String imagec = C.Get_imagec();
		String statusc = C.Get_statuscc();
		String processorc = C.Get_procesorc();
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.3.1.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(indexc)));
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.3.2.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(namec)));
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.3.3.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(imagec)));
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.3.4.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(statusc)));
		registerManagedObject(new MOScalar(new OID("1.3.6.1.3.2019.3.5.0"), MOAccessImpl.ACCESS_READ_ONLY, new OctetString(processorc)));

		}
	}

	/**
	 * Adds community to security name mappings needed for SNMPv1 and SNMPv2c.
	 */
	protected void addCommunities(SnmpCommunityMIB communityMIB) {
		UniversalVariables UV = UniversalVariables.getInstance();
		String com = UV.Get_CMS();
		Variable[] com2sec = new Variable[]{

				new OctetString(com), // community name
				new OctetString("cpublic"), // security name
				getAgent().getContextEngineID(), // local engine ID
				new OctetString("public"), // default context name
				new OctetString(), // transport tag
				new Integer32(StorageType.nonVolatile), // storage type
				new Integer32(RowStatus.active) // row status
		};
		MOTableRow row = communityMIB.getSnmpCommunityEntry().createRow(new OctetString("public2public").toSubIndex(true), com2sec);
		communityMIB.getSnmpCommunityEntry().addRow(row);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		//parametros
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
		initTableImage();
		initContainer();
		initTableStatus();
		Agent agent = new Agent("127.0.0.1/" + porta);
		agent.start();
		agent.unregisterManagedObject(agent.getSnmpv2MIB());
		while (true) {
			System.out.println("Agent running...");
			Thread.sleep(5000);
		}
	}

	public static void initparam() {
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

	public static void initTableImage() {
		int count=0;
		//Ficheiro de configuração das imagens
		List<String> lista_de_tableimage = new ArrayList<String>();
		File file = new File("resultados.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			while ((text = reader.readLine()) != null) {
				lista_de_tableimage.add(text);
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
		SingleTableImage TI = SingleTableImage.getInstance();
		for (int i = 0; i < lista_de_tableimage.size(); i++) {
			String varindimage = lista_de_tableimage.get(i);
			String[] varoid_temp = varindimage.split(Pattern.quote("."));

			String oid_table = varoid_temp[6];
			String oid_index = varoid_temp[8];
			if (oid_table.equals("2")) {
				if (oid_index.equals("2")) {
					count++;
					String[] indImage_file_split = varindimage.split(Pattern.quote("|"));
					System.out.println(Arrays.toString(indImage_file_split));
					String indImage_value = indImage_file_split[2];
					System.out.println(indImage_value);
					String index = indImage_file_split[0];
					String[] index2 = index.split(Pattern.quote("|"));
					String index3 = index2[0];
					String[]index4 = index3.split(Pattern.quote("."));
					String index5 = index4[9];
					TI.Put_ID_Image(index5,indImage_value);

				}
			}
		}
		TI.Put_size(count);
	}

	public static void initContainer() {
		//Ficheiro de configuração das imagens
		List<String> lista_de_container = new ArrayList<String>();
		File file = new File("resultados.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			while ((text = reader.readLine()) != null) {
				lista_de_container.add(text);
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
		SingleCointainer C = SingleCointainer.getInstance();
		for (int i = 0; i < lista_de_container.size(); i++) {
			String varindimage = lista_de_container.get(i);
			String[] varoid_temp = varindimage.split(Pattern.quote("."));
			String oid_objecto = varoid_temp[6];
			String oid_instancia = varoid_temp[7];
			if(oid_objecto.equals("3")){
				if(oid_instancia.equals("1")){
					String[] array_oid = varindimage.split(Pattern.quote("|"));
					String output = array_oid[2];
					C.Put_Indexc(output);
				}
				if(oid_instancia.equals("2")){
					String[] array_oid = varindimage.split(Pattern.quote("|"));
					String output = array_oid[2];
					C.Put_namec(output);
				}
				if(oid_instancia.equals("3")){
					String[] array_oid = varindimage.split(Pattern.quote("|"));
					String output = array_oid[2];
					C.Put_imagec(output);
				}
				if(oid_instancia.equals("4")){
					String[] array_oid = varindimage.split(Pattern.quote("|"));
					String output = array_oid[2];
					C.Put_statusc(output);
				}
				if(oid_instancia.equals("5")){
					String[] array_oid = varindimage.split(Pattern.quote("|"));
					String output = array_oid[2];
					C.Put_procesorc(output);
				}
			}
		}
	}
	public static void initTableStatus(){
		
	}
}

