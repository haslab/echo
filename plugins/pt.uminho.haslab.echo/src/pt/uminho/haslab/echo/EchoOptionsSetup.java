package pt.uminho.haslab.echo;

public class EchoOptionsSetup {
		
	public static int DEFAULT_BITWIDTH = 2;
	public static int DEFAULT_SCOPE = 1;
	public static int DEFAULT_DELTA = 20;
	public static boolean DEFAULT_VERBOSE = true;
	public static boolean DEFAULT_OPTIMIZE = true;
	public static boolean DEFAULT_OVERWRITE = true;
	
	
	private static EchoOptions instance;

	public static EchoOptions getInstance() {
		return instance;
	}
	
	private EchoOptionsSetup() {}

	public static void init(EchoOptions options){
		instance = options;
	}

    public interface EchoOptions {

        /** if the output should be verbose */
        public boolean isVerbose();

        /** if the generated instances should overwrite the original ones */
        public boolean isOverwrite();

        /** if Echo should try to simplify expressions */
        public boolean isOptimize();

        /** the overall Alloy scope */
        public Integer getOverallScope();

        /** the maximum delta for an instance generation run */
        public Integer getMaxDelta();

        /** the default integer bitwidth */
        public Integer getBitwidth();

        /** if the distance is operation-based */
        public boolean isOperationBased();

        /** the prefix for the qvt meta-models imports */
        public String getWorkspacePath();

        public boolean isStandalone();
    }


}
