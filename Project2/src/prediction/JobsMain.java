package prediction;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;
import org.apache.commons.lang.StringEscapeUtils;

public class JobsMain {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException, CustomExceptionHandler {
		
		//Program input validation
		if(args[0].isEmpty()) {
			throw new CustomExceptionHandler("Argument 0 cannot be empty");
		}
		
		if(args[1].isEmpty()) {
			throw new CustomExceptionHandler("Argument 1 cannot be empty");
		}
		
		File f = new File(args[0]);
		if (!f.exists() || !f.isDirectory()) {
			throw new CustomExceptionHandler("Argument 0 is invalid: Input folder " + args[0] + "does not exists");
		}
		System.out.println("file separator : "+ File.separator);
		String fileSeparator = StringEscapeUtils.escapeJava(File.separator);
		String[] strArray;
		String outputFolder = "";
		
		strArray = args[1].split(fileSeparator);
		
		//exclude filename at the end
		for (int i = 0; i < strArray.length - 1; i++) {
			outputFolder += strArray[i] + fileSeparator;
		}
		
		File f1 = new File(outputFolder);
		if (!f1.exists() || !f1.isDirectory()) {
			throw new CustomExceptionHandler("Argument 1 is invalid: Output folder " + outputFolder + "does not exists");
		}
		
		System.out.println("args[0] : "+FilenameUtils.separatorsToUnix(args[0]));
		System.out.println("args[1] : "+FilenameUtils.separatorsToUnix(args[1]));

		new Classifier(args[0], args[1]);
	}

}
