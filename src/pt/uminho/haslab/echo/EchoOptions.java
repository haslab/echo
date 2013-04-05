package pt.uminho.haslab.echo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class EchoOptions extends Options{

	private static final long serialVersionUID = 1L;
	
	CommandLine cmd;

	@SuppressWarnings("static-access")
	public EchoOptions (String[] args) throws ErrorParser {	
		super();

		this.addOption(OptionBuilder
				.withDescription("do not overwrite the original xmi")
				.withLongOpt("no-overwrite")
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
				.withDescription("verbose output")
				.withLongOpt("verbose")
				.create());
		
		this.addOption(OptionBuilder.withArgName("path")
				.hasArg()
				.isRequired(false)
				.withDescription("the QVT-R transformation file")
				.withLongOpt("qvtr")
				.create("q"));
	
		this.addOption(OptionBuilder.isRequired(false)
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
				.isRequired(false)
				.withDescription("instance files (should be in the same order as the QVT-R tranformation's arguments)")
				.create("i"));
		
		this.addOption(OptionBuilder.withArgName("paths")
				.withValueSeparator(' ')
				.withLongOpt("models")
				.hasArgs()
				.isRequired(false)
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
				.withDescription("surpress optimitazions")
				.withLongOpt("no-optimizations")
				.create("o"));
		
		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(this, args);
			if (getModels() == null || getInstances() == null) throw new Exception();
			if (isQVT() && !(isCheck() || isEnforce())) throw new Exception();
			if (isCheck() && isEnforce()) throw new Exception();
		} catch (Exception e) {
			if (this.isHelp()) {}
			else throw new ErrorParser(e.getMessage(),"CLI Parser");
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
	
	public boolean isOptimize() {
		return !cmd.hasOption("o");
	}
		
		
	public String getDirection() {
		return cmd.getOptionValue("e");
	}
	
	public String getQVTPath() {
		return cmd.getOptionValue("q");
	}
	
	public String[] getModels() {
		return cmd.getOptionValues("m");
	}

	public String[] getInstances() {
		return cmd.getOptionValues("i");
	}

	public Integer getMaxDelta() throws ErrorParser {
		Integer delta = Integer.MAX_VALUE;
		try {
			if (cmd.hasOption("d"))
				delta = (Integer) cmd.getParsedOptionValue("d");
		} catch (ParseException e) { throw new ErrorParser(e.getMessage(), "CLI Parser");}
		return delta;
	}
	
	public void printHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "echo [options] {-c|-e <direction>} -q <path> -m <paths>... -i <paths>...", this,false );
	}
	
}
