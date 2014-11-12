package prediction;

public class CustomExceptionHandler extends Exception {
	
	private String msg = null;
	
	public CustomExceptionHandler(String msg) {
		// TODO Auto-generated constructor stub
		
		this.msg = msg;
	}
	
	public String getMessage() {
		return msg;
	}

}
