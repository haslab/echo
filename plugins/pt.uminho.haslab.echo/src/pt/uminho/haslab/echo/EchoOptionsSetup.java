package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.engine.CoreFactory;

/**
 * Provides the interface and default values for Echo's parameters.
 * Should be instantiated by concrete option manager.
 * 
 * @author nmm
 * @version 0.4 23/03/2015
 */
public class EchoOptionsSetup {
		
	/** identifiers for the options. */
	public static final String BITWIDTH = "bitwidth";
	public static final String SCOPE = "scope";
	public static final String DELTA = "delta";
	public static final String OPTIMIZE = "optimize";
	public static final String OVERWRITE = "overwrite";
	public static final String VERBOSE = "verbose";
	public static final String CORE = "engine";
	public static final String DISTANCE = "distance";
	
	/** default values for the options. */
	public static final int DEFAULT_BITWIDTH = 4;
	public static final int DEFAULT_SCOPE = 3;
	public static final int DEFAULT_DELTA = 10;
	public static final boolean DEFAULT_DISTANCE = false;
	public static final boolean DEFAULT_VERBOSE = true;
	public static final boolean DEFAULT_OPTIMIZE = true;
	public static final boolean DEFAULT_OVERWRITE = true;
	public static final CoreFactory DEFAULT_ENGINE = CoreFactory.ALLOY;
	
	private static EchoOptions instance;

	public static EchoOptions getInstance() {
		return instance;
	}
	
	private EchoOptionsSetup() {}

	public static void init(EchoOptions options) {
		instance = options;
	}
	
	/**
	 * Retrieves the option value from the identifier.
	 * @param identifier the option identifier.
	 * @return the option value.
	 */
	public static Object optionValue(String identifier) {
		switch (identifier) {
		case BITWIDTH : return instance.getBitwidth();
		case SCOPE : return instance.getOverallScope();
		case DELTA : return instance.getMaxDelta();
		case OVERWRITE : return instance.isOverwrite();
		case OPTIMIZE : return instance.isOptimize();
		case VERBOSE : return instance.isVerbose();
		case DISTANCE : return instance.isOperationBased();
		case CORE : return instance.getCore();
		default: return null;
		}
	}

    public interface EchoOptions {
        /** the overall scope, used as a default. */
        public Integer getOverallScope();

        /** the maximum delta for action tasks. */
        public Integer getMaxDelta();

        /** the integer bitwidth. */
        public Integer getBitwidth();

        /** whether the distance in action tasks is to be operation-based (graph-edit distance otherwise). */
        public boolean isOperationBased();

        /** the core engine being used, either Alloy or Kodkod. */
        public CoreFactory getCore();

        /** whether Echo is being run as standalone (in contrast to Eclipse plugin). */
        public boolean isStandalone();
        
        /** whether generated instances should overwrite the original ones. */
        public boolean isOverwrite();

        /** whether Echo should apply performance optimizations. */
        public boolean isOptimize();

        /** whether the output is to be verbose */
        public boolean isVerbose();
    }

}
