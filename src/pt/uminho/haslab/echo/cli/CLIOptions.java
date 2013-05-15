package pt.uminho.haslab.echo.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import pt.uminho.haslab.echo.EchoOptions;
import pt.uminho.haslab.echo.ErrorParser;

public class CLIOptions extends Options implements EchoOptions{

	private static final long serialVersionUID = 1L;
	
	CommandLine cmd;

	@SuppressWarnings("static-access")
	public CLIOptions (String[] args) throws ErrorParser {	
		super();
		
		this.addOption(OptionBuilder
				.withDescription("generate new QVT consistent target instance")
				.withLongOpt("new")
				.create("n"));
		
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
				.withDescription("surpress optimitazions")
				.withLongOpt("no-optimizations")
				.create("o"));
		
		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(this, args);
			if (isGenerate() && (isQVT() || isConformance() || cmd.getOptionValues("i") != null)) 
				throw new ErrorParser("Cannot perform tests and generate instances.");
			if (isRepair() && (isQVT() || isConformance())) 
				throw new ErrorParser("Cannot perform tests and repair instances.");
			if (getModels() == null) 
				throw new ErrorParser("Metamodels required","CLI Parser");
			if (isQVT() && !(isCheck() || isEnforce()))
				throw new ErrorParser("Applying QVT transformation requires running mode.");
			if (isCheck() && isEnforce()) 
				throw new ErrorParser("Choose either enforce or check mode.");
		} catch (Exception e) {
			if (!(Arrays.asList(args)).contains("--help"))
				throw new ErrorParser(e.getMessage());
		}
		
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

	public boolean isNew() {
		return cmd.hasOption("new");
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
		else return cmd.getOptionValue("e");
	}
	
	public Integer getOverallScope() {
		Integer size = 0;
		try {size = Integer.parseInt(cmd.getOptionValue("g"));}
		catch (Exception x) { size = 0; }
		return size;
	}
	
	public String getQVTPath() {
		return cmd.getOptionValue("q");
	}
	
	public String[] getModels() {
		return cmd.getOptionValues("m");
	}

	public String[] getInstances() {
		String [] res = cmd.getOptionValues("i");
		if (res == null) res = new String[0];
		return res;
	}
	
	public Map<Entry<String,String>,Integer> getScopes() {
		Map<Entry<String,String>,Integer> res = new HashMap<Entry<String,String>,Integer>();
		String[] args = cmd.getOptionValues("scopes");
		if (args != null) {
			for (int i = 0; i < args.length ; i++) {
				String[] split = args[i].split("::");
				if (split.length == 2)
					res.put(new SimpleEntry<String,String>(split[0],split[1]),Integer.parseInt(args[++i]));
				else if (split.length == 1)
					res.put(new SimpleEntry<String,String>("",split[0]),Integer.parseInt(args[++i]));
			}
		}
		return res;
	}


	public Integer getMaxDelta() {
		Integer delta = Integer.MAX_VALUE;
		try {
			if (cmd.hasOption("d"))
				delta = (Integer) cmd.getParsedOptionValue("d");
		} catch (ParseException e) { delta = 0; }
		return delta;
	}
	
	public Integer getBitwidth() {
		Integer delta = 2;
		try {
			if (cmd.hasOption("bitwidth"))
				delta = Integer.parseInt(cmd.getOptionValue("bitwidth"));
		} catch (Exception x) { delta = 2; }
		return delta;
	}
	
	public void printHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("echo [options] {-c|-e <direction>} -q <path> -m <paths>... -i <paths>...", this,false );
	}
	
	public boolean isOperationBased(){
		return cmd.hasOption("operation");
	}

}
