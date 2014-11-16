package prediction;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;
import org.apache.commons.lang.StringEscapeUtils;

public class JobsMain {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException, CustomExceptionHandler, java.text.ParseException {
		
		String outputFilePath = args[1];
		
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
		
//		for (int i = 0; i < strArray.length; i++) {
//			System.out.println(strArray[i]);
//		}
		
		//exclude filename at the end
		for (int i = 0; i < strArray.length - 1; i++) {
			outputFolder += strArray[i] + fileSeparator;
		}
		
		File f1 = new File(outputFolder);
		if (!f1.exists() || !f1.isDirectory()) {
			throw new CustomExceptionHandler("Argument 1 is invalid: Output folder " + outputFolder + "does not exists");
		}
		
		//args[1] = outputFolder + strArray[strArray.length];
		
		System.out.println("args[0] : "+FilenameUtils.separatorsToUnix(args[0]));
		System.out.println("args[1] : "+FilenameUtils.separatorsToUnix(outputFilePath));

		new Classifier(args[0], outputFilePath);
	}

}
