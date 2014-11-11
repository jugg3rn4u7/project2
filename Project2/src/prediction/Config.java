package prediction;
public class Config {

	private int skipInputFileLoading = 0;
	private int skipNeighborCalculation = 0;

	public int getSkipInputFileLoading() {
		return skipInputFileLoading;
	}

	public void setSkipInputFileLoading(int skipInputFileLoading) {
		this.skipInputFileLoading = skipInputFileLoading;
	}

	public int getSkipNeighborCalculation() {
		return skipNeighborCalculation;
	}

	public void setSkipNeighborCalculation(int skipNeighborCalculation) {
		this.skipNeighborCalculation = skipNeighborCalculation;
	}

}
