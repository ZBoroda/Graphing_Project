package project;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] xy = {3,1};
		double slope = 1;
		double solution = -xy[1];
		solution += slope*xy[0];
		solution /= slope;
		System.out.println(solution);
	}

}
