import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RealAdaBoosting {

	private static String filename;
	private  int T;
	private int n;
	private double epsilon;
	private double training_set_x[];
	private double training_set_y[];
	private double training_set_p[][];		// At index 0 - initial probabilities, from 1 to T+1 - new probabilities
	private double threshold_array[];
	private boolean equality_array[];		// True for representing <=, false for >
	private double P_right_positive_array[];
	private double P_right_negative_array[];
	private double P_wrong_positive_array[];
	private double P_wrong_negative_array[];
	private double G_array[];
	private double c_positive[];
	private double c_negative[];
	private double f[][];
	private double bound;

	public RealAdaBoosting() {
		super();
		filename = "C:/Users/Anvesh/Google Drive/workspace/ML2/src/file.txt";
		this.T = 0;
		this.n = 0;
		this.epsilon = 0.0;
		this.bound = 1.0;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		RealAdaBoosting.filename = filename;
	}

	public int getT() {
		return T;
	}

	public void setT(int t) {
		T = t;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public double[] getTraining_set_x() {
		return training_set_x;
	}

	public void setTraining_set_x(double[] training_set_x) {
		this.training_set_x = training_set_x;
	}

	public double[] getTraining_set_y() {
		return training_set_y;
	}

	public void setTraining_set_y(double[] training_set_y) {
		this.training_set_y = training_set_y;
	}

	public double[][] getTraining_set_p() {
		return training_set_p;
	}

	public void setTraining_set_p(double[][] training_set_p) {
		this.training_set_p = training_set_p;
	}

	public double[] getThreshold_array() {
		return threshold_array;
	}

	public void setThreshold_array(double[] threshold_array) {
		this.threshold_array = threshold_array;
	}

	public double[] getG_array() {
		return G_array;
	}

	public void setG_array(double[] G_array) {
		this.G_array = G_array;
	}

	public boolean[] getEquality_array() {
		return equality_array;
	}

	public void setEquality_array(boolean[] equality_array) {
		this.equality_array = equality_array;
	}

	public double[] getC_positive() {
		return c_positive;
	}

	public void setC_positive(double[] c_positive) {
		this.c_positive = c_positive;
	}

	public double[] getC_negative() {
		return c_negative;
	}

	public void setC_negative(double[] c_negative) {
		this.c_negative = c_negative;
	}
	
	public double getBound() {
		return bound;
	}

	public void setBound(double bound) {
		this.bound = bound;
	}
	
	public double[] getP_right_positive_array() {
		return P_right_positive_array;
	}

	public void setP_right_positive_array(double[] p_right_positive_array) {
		P_right_positive_array = p_right_positive_array;
	}

	public double[] getP_right_negative_array() {
		return P_right_negative_array;
	}

	public void setP_right_negative_array(double[] p_right_negative_array) {
		P_right_negative_array = p_right_negative_array;
	}

	public double[] getP_wrong_positive_array() {
		return P_wrong_positive_array;
	}

	public void setP_wrong_positive_array(double[] p_wrong_positive_array) {
		P_wrong_positive_array = p_wrong_positive_array;
	}

	public double[] getP_wrong_negative_array() {
		return P_wrong_negative_array;
	}

	public void setP_wrong_negative_array(double[] p_wrong_negative_array) {
		P_wrong_negative_array = p_wrong_negative_array;
	}

	public double[][] getF() {
		return f;
	}

	public void setF(double[][] f) {
		this.f = f;
	}

	
	
	public void readFile(String fname) {

		File file = new File(fname);
		Scanner sc;
		String line = null;

		try {
			sc = new Scanner(file);

			if(sc.hasNextLine()) {
				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == 3){
					this.T = Integer.parseInt(tokens[0]);
					this.n = Integer.parseInt(tokens[1]);
					this.epsilon = Double.parseDouble(tokens[2]);
					this.training_set_x = new double[this.n];
					this.training_set_y = new double[this.n];
					this.training_set_p = new double[this.T+1][this.n];
					this.threshold_array = new double[this.T];
					this.P_right_positive_array = new double[this.T];;
					this.P_right_negative_array = new double[this.T];;
					this.P_wrong_positive_array = new double[this.T];;
					this.P_wrong_negative_array = new double[this.T];;
					this.G_array = new double[this.T];
					this.equality_array = new boolean[this.T];
					this.c_positive = new double[this.T];
					this.c_negative = new double[this.T];
					this.f = new double[this.T][this.n];
				} else {
					
					System.out.println("Incorrect file input!");
					System.exit(-1);
					
				}				
			} else {
				
				System.out.println("No data in file!");
				System.exit(-1);
				
			}

			if(sc.hasNextLine()) {

				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == this.n){
					for(int i = 0;i<this.n;i++) {
						this.training_set_x[i] = Double.parseDouble(tokens[i]);
					}	
				} else {
					System.out.println("Incorrect file input!");
					System.exit(-1);
				}		

			} else {
				System.out.println("No data in file!");
				System.exit(-1);
			}


			if(sc.hasNextLine()) {

				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == this.n){
					for(int i = 0;i<this.n;i++) {
						this.training_set_y[i] = Double.parseDouble(tokens[i]);
					}	
				} else {
					System.out.println("Incorrect file input!");
					System.exit(-1);
				}		

			} else {
				System.out.println("No data in file!");
				System.exit(-1);
			}


			if(sc.hasNextLine()) {

				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == this.n){
					for(int i = 0;i<this.n;i++) {
						this.training_set_p[0][i] = Double.parseDouble(tokens[i]);		// At index 0 - initial probabilities, from 1 to T+1 - new probabilities
					}	
				} else {
					System.out.println("Incorrect file input!");
					System.exit(-1);
				}		

			} else {
				System.out.println("No data in file!");
				System.exit(-1);
			}

			sc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	
	public void findHypothesis(int iteration) {

		double threshold = 0.0;
		double less_than_threshold = 0.0;
		double greater_than_threshold = 0.0;

		double G;
		double less_than_G = Double.MAX_VALUE;
		double greater_than_G = Double.MAX_VALUE;

		double lt_P_right_positive = 0.0;
		double lt_P_right_negative = 0.0;
		double lt_P_wrong_positive = 0.0;
		double lt_P_wrong_negative = 0.0;
		
		double gt_P_right_positive = 0.0;
		double gt_P_right_negative = 0.0;
		double gt_P_wrong_positive = 0.0;
		double gt_P_wrong_negative = 0.0;
		
		boolean useLessThan = true;

		for(int i = 0;i<this.n-1;i++) {

			double local_threshold = (this.training_set_x[i] + this.training_set_x[i+1])/2;

			double P_right_positive = 0.0;
			double P_right_negative = 0.0;
			double P_wrong_positive = 0.0;
			double P_wrong_negative = 0.0;
			double local_G = Double.MAX_VALUE;
			

			for(int j = 0; j < this.n;j++) {
				if(this.training_set_x[j] < local_threshold) {
					if(this.training_set_y[j] == 1) 
						P_right_positive += this.training_set_p[iteration][j];			// h(x) = 1, y = 1
					else 
						P_wrong_negative += this.training_set_p[iteration][j];			// h(x) = 1, y = -1
				} else {
					if(this.training_set_y[j] == 1)
						P_wrong_positive += this.training_set_p[iteration][j];			// h(x) = -1, y = 1
					else
						P_right_negative += this.training_set_p[iteration][j];			// h(x) = -1, y = -1
				}
			}

			local_G = (Math.sqrt(P_right_positive*P_wrong_negative)) + (Math.sqrt(P_wrong_positive*P_right_negative));
			
			if(local_G<less_than_G) {
				less_than_G = local_G;
				less_than_threshold = local_threshold;
				lt_P_right_positive = P_right_positive;
				lt_P_right_negative = P_right_negative;
				lt_P_wrong_positive = P_wrong_positive;
				lt_P_wrong_negative = P_wrong_negative;
			}
		}

		for(int i = 0;i<this.n-1;i++) {

			double local_threshold = (this.training_set_x[i] + this.training_set_x[i+1])/2;

			double P_right_positive = 0.0;
			double P_right_negative = 0.0;
			double P_wrong_positive = 0.0;
			double P_wrong_negative = 0.0;
			double local_G = Double.MAX_VALUE;

			for(int j = 0; j < this.n;j++) {
				if(this.training_set_x[j] > local_threshold) {
					if(this.training_set_y[j] == 1) 
						P_right_positive += this.training_set_p[iteration][j];			// h(x) = 1, y = 1
					else 
						P_wrong_negative += this.training_set_p[iteration][j];			// h(x) = 1, y = -1
				} else {
					if(this.training_set_y[j] == 1)
						P_wrong_positive += this.training_set_p[iteration][j];			// h(x) = -1, y = 1
					else
						P_right_negative += this.training_set_p[iteration][j];			// h(x) = -1, y = -1
				}
			}

			local_G = (Math.sqrt(P_right_positive*P_wrong_negative)) + (Math.sqrt(P_wrong_positive*P_right_negative));
			
			if(local_G<greater_than_G) {
				greater_than_G = local_G;
				greater_than_threshold = local_threshold;
				gt_P_right_positive = P_right_positive;
				gt_P_right_negative = P_right_negative;
				gt_P_wrong_positive = P_wrong_positive;
				gt_P_wrong_negative = P_wrong_negative;
			}
		}

		if(greater_than_G<less_than_G) {
			G  = greater_than_G;
			threshold = greater_than_threshold;
			useLessThan = false;
			this.P_right_positive_array[iteration] = gt_P_right_positive;
			this.P_wrong_negative_array[iteration] = gt_P_wrong_negative;
			this.P_wrong_positive_array[iteration] = gt_P_wrong_positive;
			this.P_right_negative_array[iteration] = gt_P_right_negative;
		} else {
			G = less_than_G;
			threshold = less_than_threshold;
			this.P_right_positive_array[iteration] = lt_P_right_positive;
			this.P_wrong_negative_array[iteration] = lt_P_wrong_negative;
			this.P_wrong_positive_array[iteration] = lt_P_wrong_positive;
			this.P_right_negative_array[iteration] = lt_P_right_negative;
		}

		this.threshold_array[iteration] = threshold;
		this.G_array[iteration] = G;
		this.equality_array[iteration] = useLessThan;
	}
	
	
	
	public void compute_new_probabilities(int iteration, double threshold_per_iteration, boolean equality_per_iteration, double normalization_factor) {

		for(int i = 0;i<this.n;i++) {
			//  check classifier, whether < or >
			if(equality_per_iteration) {
				// if classifier is <, then x<v should have y=1, rest y=-1 
				if(this.training_set_x[i]<threshold_per_iteration) {

					this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * (Math.exp(-(this.training_set_y[i]*this.c_positive[iteration]))))/normalization_factor;

				} else {		

					this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * (Math.exp(-(this.training_set_y[i]*this.c_negative[iteration]))))/normalization_factor;

				}		
			} else {	
				// if classifier is >, then x>v should have y=1, rest y=-1
				if(this.training_set_x[i]>threshold_per_iteration) {

					this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * (Math.exp(-(this.training_set_y[i]*this.c_positive[iteration]))))/normalization_factor;

				} else {

					this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * (Math.exp(-(this.training_set_y[i]*this.c_negative[iteration]))))/normalization_factor;

				}	
			}
		}

//		System.out.println();
//		System.out.println();
//		System.out.println("New probabilities after normalization for interation " + (iteration+1) + " : ");
//		System.out.println("-------------------------------------");
//		System.out.println("index || new probabilities");
//		System.out.println("-------------------------------------");
		
		System.out.print("Pi after normalization = ");
		
		for(int k = 0;k<this.n;k++) {
//			System.out.print(k + "     || ");
//			System.out.print(this.training_set_p[iteration+1][k]);
//			System.out.println();
			System.out.print(this.training_set_p[iteration+1][k]);
			if(k!=this.n-1)
				System.out.print(", ");
			else 
				System.out.print(" ");
		}	
//		System.out.println("-------------------------------------");
		System.out.println();
	}

	
	
	public void classify_using_boosted_classifier(int example_no,int iteration) {


		for(int i = 0;i<=iteration;i++) {

			boolean equality = this.equality_array[i];

			//			System.out.print(equality + " ");
			//			System.out.print(this.training_set_x[example_no] + " ");
			//			System.out.print(this.threshold_array[i]);
			//			System.out.println();

			if(equality) {

				if(this.training_set_x[example_no] < this.threshold_array[i]) {
					if(iteration == 0) 
						this.f[iteration][example_no] = this.c_positive[iteration];
					else
						this.f[iteration][example_no] = this.f[iteration-1][example_no] + this.c_positive[iteration];
				}
				else { 
					if(iteration == 0)
						this.f[iteration][example_no] = this.c_negative[iteration];
					else
						this.f[iteration][example_no] = this.f[iteration-1][example_no] + this.c_negative[iteration];
				}
				
			} else {

				if(this.training_set_x[example_no] > this.threshold_array[i]) {
					if(iteration == 0) 
						this.f[iteration][example_no] = this.c_positive[iteration];
					else
						this.f[iteration][example_no] = this.f[iteration-1][example_no] + this.c_positive[iteration];
				}
				else { 
					if(iteration == 0)
						this.f[iteration][example_no] = this.c_negative[iteration];
					else
						this.f[iteration][example_no] = this.f[iteration-1][example_no] + this.c_negative[iteration];
				}
			}
		}
	}
	
	
	
	public void compute_boosted_classifier(int iteration) {

		double count = 0;

//		System.out.println();

		System.out.print("f(x) = "); 
		
		for(int i = 0;i<this.n;i++) {

			classify_using_boosted_classifier(i,iteration);

			System.out.print(this.f[iteration][i]);
			if(i!=this.n-1)
				System.out.print(", ");
			else 
				System.out.print(" ");
//			System.out.print("Iteration " + (iteration+1) + " - The training example " + i + " : f(x) = " + this.f[iteration][i]);

			double y = 0.0;

			if(this.f[iteration][i]<0) {
				y = -1.0;				
			} else {
				y = 1.0;
			}

			if(y == this.training_set_y[i]) {
//				System.out.print(" And classified correctly!");
			} else {
				count++;
//				System.out.print(" And classified wrongly!");
			}

//			System.out.println();
		}

		System.out.println();

//		System.out.println("Mistakes in this iteration " + (iteration+1) + " are : " + count);

		System.out.println("Boosted Classifier Error = " + count/this.n);
//		System.out.println("Error of boosted classifier so far (E) = " + count/this.n);
	}
	
	
	
	public void runSingleIteration(int iteration) {

		findHypothesis(iteration);

		double threshold_per_iteration = this.threshold_array[iteration];
		double G_per_iteration = this.G_array[iteration];
		boolean equality_per_iteration = this.equality_array[iteration];		// True for representing <=, false for >
		String selected_classifier = "";

		if(equality_per_iteration) {
			selected_classifier = "I(x < " + threshold_per_iteration + ")";
		} else {
			selected_classifier = "I(x > " + threshold_per_iteration + ")";
		}



		System.out.println("Classifier h = " + selected_classifier);
//		System.out.println("Selected weak classifier for iteration " + (iteration+1) + " is : " + selected_classifier);



		System.out.println("G error = " + G_per_iteration);
//		System.out.println("G Error value of selected classifier " + selected_classifier + " : " + G_per_iteration);



		this.c_positive[iteration] = (0.5) * (Math.log((this.P_right_positive_array[iteration]+this.epsilon)/(this.P_wrong_negative_array[iteration]+this.epsilon)));
		this.c_negative[iteration] = (0.5) * (Math.log((this.P_wrong_positive_array[iteration]+this.epsilon)/(this.P_right_negative_array[iteration]+this.epsilon)));
//		System.out.println("Weights of selected classifier " + selected_classifier + " for iteration " + (iteration+1) + " : ");
//		System.out.println("c_positive : " + this.c_positive[iteration]);
//		System.out.println("c_negative : " + this.c_negative[iteration]);
		System.out.println("C_Plus = " + this.c_positive[iteration] + ", C_Minus = " + this.c_negative[iteration]);


		double normalization_factor = 2 * G_per_iteration;		
		System.out.println("Normalization factor Z = " + normalization_factor);
//		System.out.println("The probabilities normalization factor (Z) of selected classifier " + selected_classifier + " : " + normalization_factor);



		compute_new_probabilities(iteration,threshold_per_iteration,equality_per_iteration,normalization_factor);



		compute_boosted_classifier(iteration);



		this.bound = this.bound * normalization_factor;
		System.out.println("Bound on Error = " + this.bound);
//		System.out.println("Bound on error of boosted classifier (E) : " + this.bound);

	}

	
	// Run Real Adaptive Boosting 
	public void runRealAdaBoosting() {

		for(int i=0;i<this.T;i++) {

//			System.out.println();
//			System.out.println();
//			System.out.println("--------------------------------Iteration " + i + " ------------------------------------------");

			System.out.println("Iteration " + (i+1));
			
			runSingleIteration(i);		// Run a single iteration

			System.out.println();
//			System.out.println("--------------------------------------------------------------------------------------");


			if(this.G_array[i] >= 0.5) {

				System.out.println("Select a different subset of the training examples.");
				System.exit(-1);

			}
		}
	}


	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//		double d= 0.000001;
		//		System.out.printf("%f",d);

		Scanner sc = new Scanner(System.in);
		
		RealAdaBoosting rab = new RealAdaBoosting();

//		System.out.println("Enter the full-file path for data file : ");
		
//		rab.readFile(sc.nextLine().toString());
		
//		rab.readFile(filename);

//		rab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-1.dat");
//		rab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-2.dat");
//		rab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-3.dat");
//		rab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-4.dat");
		rab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-5.dat");

//		System.out.println("Contents of the file : ");
//		System.out.println(rab.T + " " + rab.n + " " + rab.epsilon);
//
//		for(int i=0;i<rab.n;i++) {
//			System.out.print(rab.training_set_x[i] + " ");
//			System.out.print(rab.training_set_y[i] + " ");
//			System.out.print(rab.training_set_p[0][i] + " ");
//			System.out.println();
//		}		

		rab.runRealAdaBoosting();

		sc.close();
		
	}

}
