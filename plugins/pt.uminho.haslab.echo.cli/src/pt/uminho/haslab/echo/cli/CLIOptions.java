package pt.uminho.haslab.echo.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoOptionsSetup.EchoOptions;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.CoreFactory;

public class CLIOptions extends Options implements EchoOptions{

	private static final long serialVersionUID = 1L;
	
	CommandLine cmd;

	@SuppressWarnings("static-access")
	public CLIOptions (String[] args) throws EErrorParser {	
		super();
		
		this.addOption(OptionBuilder
				.withDescription("generate new QVT consistent target instance")
				.withLongOpt("batch")
				.hasArg()
				.withArgName("direction")
				.create("b"));
				
		this.addOption(OptionBuilder
				.withDescription("do not overwrite the original xmi")
				.withLongOpt("no-overwrite")
				.create());
		
		this.addOption(OptionBuilder
				.withDescription("use operation-based metric")
				.withLongOpt("operation")
				.create());
		
		this.addOption(OptionBuilder
				.withDescription("number of elements of a given class")
				.withLongOpt("scopes")
				.withArgName("scopes")
				.withValueSeparator(' ')
				.hasArgs()
				.create());

		this.addOption(OptionBuilder
				.withDescription("checkonly mode")
				.withLongOpt("check")
				.create("c"));
		
		this.addOption(OptionBuilder
				.hasArg()
				.withArgName("direction")
				.withDescription("enforce mode")
				.withLongOpt("enforce")
				.create("e"));

		this.addOption(OptionBuilder
				.hasArg()
				.withArgName("direction")
				.withDescription("repair mode")
				.withLongOpt("repair")
				.create("r"));

		this.addOption(OptionBuilder
				.hasArg()
				.withArgName("size")
				.withDescription("generate instances conformant to the metamodels")
				.withLongOpt("generate")
				.withType(Integer.class)
				.create("g"));

		this.addOption(OptionBuilder
				.withDescription("verbose output")
				.withLongOpt("verbose")
				.create());
		
		this.addOption(OptionBuilder.withArgName("path")
				.hasArg()
				.withDescription("the QVT-R transformation file")
				.withLongOpt("qvtr")
				.create("q"));
	
		this.addOption(OptionBuilder
				.withDescription("test if the instances conform to the models")
				.withLongOpt("conformance")
				.create("t"));
	
		this.addOption(OptionBuilder.withDescription("prints this message")
				.withLongOpt("help")
				.create());
	
		this.addOption(OptionBuilder.withArgName("paths")
				.withValueSeparator(' ')
				.withLongOpt("instances")
				.hasArgs()
				.withDescription("instance files (should be in the same order as the QVT-R tranformation's arguments)")
				.create("i"));
		
		this.addOption(OptionBuilder.withArgName("paths")
				.withValueSeparator(' ')
				.withLongOpt("models")
				.hasArgs()
				.withDescription("model files")
				.create("m"));
		
		this.addOption(OptionBuilder
				.hasArg()
				.withArgName("nat")
				.withDescription("maximum delta")
				.withLongOpt("delta")
				.withType(Integer.class)
				.create("d"));
		
		this.addOption(OptionBuilder
				.hasArg()
				.withArgName("nat")
				.withDescription("default integer bitwidth")
				.withLongOpt("bitwidth")
				.withType(Integer.class)
				.create());
		
		this.addOption(OptionBuilder
				.withDescription("surpress optimizations")
				.withLongOpt("no-optimizations")
				.create("o"));

		this.addOption(OptionBuilder
				.hasArg()
				.withArgName("core")
				.withDescription("core engine")
				.withLongOpt("core")
				.withType(String.class)
				.create());

		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(this, args);
			if (isGenerate() && (isQVT() || isConformance())) 
				throw new EErrorParser(EErrorParser.MODE,"Cannot perform tests and generate instances.",Task.ECHO_RUN);
			if (isRepair() && (isQVT() || isConformance())) 
				throw new EErrorParser(EErrorParser.MODE,"Cannot perform tests and repair instances.",Task.ECHO_RUN);
			if (getMetamodels() == null) 
				throw new EErrorParser(EErrorParser.MODE,"Metamodels required","CLI Parser",Task.ECHO_RUN);
			if (isQVT() && !(isCheck() || isEnforce() || isBatch()))
				throw new EErrorParser(EErrorParser.MODE,"Applying QVT transformation requires running mode.",Task.ECHO_RUN);
			if (isCheck() && isEnforce()) 
				throw new EErrorParser(EErrorParser.MODE,"Choose either enforce or check mode.",Task.ECHO_RUN);
			if (getCore() == null) 
				throw new EErrorParser(EErrorParser.MODE,"Choose either 'alloy' or 'kodkod' core engine.",Task.ECHO_RUN);
		} catch (Exception e) {
			if (e instanceof EErrorParser) throw (EErrorParser) e;
			if (!(Arrays.asList(args)).contains("--help"))
				throw new EErrorParser(EErrorParser.MODE,e.getMessage(),Task.ECHO_RUN);
		}
	}

	public CoreFactory getCore() {
		if (!cmd.hasOption("core"))	return EchoOptionsSetup.DEFAULT_ENGINE;
		if (cmd.getOptionValue("core").toLowerCase().equals("alloy")) return CoreFactory.ALLOY;
		if (cmd.getOptionValue("core").toLowerCase().equals("kodkod")) return CoreFactory.KODKOD;
		return null;
	}
	
	public boolean isVerbose() {
		return cmd.hasOption("verbose");
	}
	
	public boolean isCheck() {
		return cmd.hasOption("c");
	}

	public boolean isEnforce() {
		return cmd.hasOption("e");
	}

	public boolean isConformance() {
		return cmd.hasOption("t");
	}

	public boolean isQVT() {
		return cmd.hasOption("q");
	}

	public boolean isHelp() {
		return cmd.hasOption("h");
	}
	
	public boolean isOverwrite() {
		return !cmd.hasOption("no-overwrite");
	}

	public boolean isBatch() {
		return cmd.hasOption("batch");
	}

	public boolean isOptimize() {
		return !cmd.hasOption("o");
	}
	
	public boolean isGenerate() {
		return cmd.hasOption("g");
	}

	public boolean isRepair() {
		return cmd.hasOption("r");
	}

	public String getDirection() {
		if (cmd.hasOption("r")) return cmd.getOptionValue("r");
		else if (cmd.hasOption("g")) return cmd.getOptionValue("g");
		else if (cmd.hasOption("b")) return cmd.getOptionValue("b");
		else return cmd.getOptionValue("e");
	}
	
	public Integer getOverallScope() {
		Integer size = EchoOptionsSetup.DEFAULT_SCOPE;
		return size;
	}
	
	public String getQVTURI() {
		return cmd.getOptionValue("q");
	}
	
	public String[] getMetamodels() {
		String[] x = cmd.getOptionValues("m");
		return x;
	}

	public String[] getModels() {
		String [] res = cmd.getOptionValues("i");
		if (res == null) res = new String[0];
		return res;
	}
	
	public Map<String,Map<String,Integer>> getScopes(String metamodelID) {
		Map<String,Map<String,Integer>> res = new HashMap<String,Map<String,Integer>>();
		String[] args = cmd.getOptionValues("scopes");
		Map<String,Integer> aux = new HashMap<String,Integer>();
		if (args != null) {
			for (int i = 0; i < args.length ; i++) {
				aux.put(args[i],Integer.parseInt(args[++i]));
//				String[] split = args[i].split("::");
//				if (split.length == 2)
//					res.put(new SimpleEntry<String,String>(split[0],split[1]),Integer.parseInt(args[++i]));
//				else if (split.length == 1)
//					res.put(new SimpleEntry<String,String>("",split[0]),Integer.parseInt(args[++i]));
			}
		}
		res.put(metamodelID, aux);
		return res;
	}


	public Integer getMaxDelta() {
		Integer delta = EchoOptionsSetup.DEFAULT_DELTA;
		try {
			if (cmd.hasOption("d"))
				delta = Integer.parseInt(cmd.getOptionValue("d"));
		} catch (Exception e) { 
			delta = EchoOptionsSetup.DEFAULT_DELTA;
		}
		return delta;
	}
	
	public Integer getBitwidth() {
		Integer delta = EchoOptionsSetup.DEFAULT_BITWIDTH;
		try {
			if (cmd.hasOption("bitwidth"))
				delta = Integer.parseInt(cmd.getOptionValue("bitwidth"));
		} catch (Exception x) { delta =  EchoOptionsSetup.DEFAULT_BITWIDTH; }
		return delta;
	}
	
	public void printHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("echo [options] {-c|-e <direction>} -q <path> -m <paths>... -i <paths>...", this,false );
	}
	
	public boolean isOperationBased(){
		return cmd.hasOption("operation");
	}

	@Override
	public boolean isStandalone() {
		return true;
	}

}
