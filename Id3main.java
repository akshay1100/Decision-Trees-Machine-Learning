import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.*; 
//creating node
class Id_3 {
	private Id_3 lt;
	private Id_3 rt;
	private boolean lfNode;
	private String name;
	private int nodeNum;
	private String Value;
	private static int depth = -1;
	private Set<String> attr;
	
	public Id_3() {
		super();
	}
	
	public Id_3(String attr, Id_3 lt, Id_3 rt){
		this.name = attr;
		this.lt = lt;
		this.rt = rt;
		this.setLfNode(Boolean.FALSE);
	}
	
	public Id_3(String Value){
		this.Value = Value;
		this.setLfNode(Boolean.TRUE);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Set<String> getAttributes() {
		return attr;
	}

	public void setAttributes(Set<String> attr) {
		this.attr = attr;
	}
	
	public Id_3 getLt() {
		return lt;
	}

	public void setLt(Id_3 lt) {
		this.lt = lt;
	}

	public Id_3 getRt() {
		return rt;
	}

	public void setRt(Id_3 rt) {
		this.rt = rt;
	}
	
	
	public boolean isLfNode() {
		return lfNode;
	}

	public void setLfNode(boolean lfNode) {
		this.lfNode = lfNode;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String Value) {
		this.Value = Value;
	}
	
	public int getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(int nodeNumber) {
		this.nodeNum = nodeNumber;
	}
		
	public void print(){
		depth++;
		if(this.name == null){
			System.out.print(" : " + Value);
		}
		else{
			System.out.println();
			for(int i=0; i<depth;i++){
				System.out.print(" | ");
			}
			System.out.print(name + " = 0");
		}

		if(lt != null){
			lt.print();
			if(this.name == null){
				System.out.print(" : " + Value);
			}
			else{
				System.out.println();
				for(int i=0; i<depth;i++){
					System.out.print(" | ");
				}
				System.out.print(name + " = 1" );
			}
			rt.print();
		}
		depth--;
	}	
}

class Id_tree {

	private int yy = 0;

	public Id_3 treeBuild(ArrayList<ArrayList<String>> Set, ArrayList<String> attrList, boolean fg) throws FileNotFoundException{
		int zeroCount = 0;
		int oneCount = 0;

		for(int i=1; i < Set.size();i++){
			if(Set.get(i).get(Set.get(i).size()-1).equalsIgnoreCase("1")){
				oneCount++;
			}
			else{
				zeroCount++;
			}
		}
		if (attrList.isEmpty() || zeroCount == Set.size()-1){
			return new Id_3("0");

		}
		else if(attrList.isEmpty() || oneCount == Set.size()-1){
			return new Id_3("1");
		}
		else{

			gainCal gainCal = new gainCal();
			String attributeSelec = gainCal.attributeSelec(Set,attrList,fg);

			ArrayList<String> attr = new ArrayList<String>();

			HashMap<String, ArrayList<ArrayList<String>>> map1 = gainCal.mapBestAttr(Set, attributeSelec);
			for(String att: attrList){
				if(!att.equalsIgnoreCase(attributeSelec)){
					attr.add(att);
				}
			}


			if (map1.size() < 2){
				String value = "0";
				if(oneCount > zeroCount){
					value = "1";
				}

				return new Id_3(value);
			}


			return new Id_3(attributeSelec, treeBuild(map1.get("0"),attr,fg),treeBuild(map1.get("1"),attr,fg));
		}
	}
	
	public void mkTrCp(Id_3 first, Id_3 second){
		second.setLfNode(first.isLfNode());
		second.setName(first.getName());
		second.setValue(first.getValue());

		if(!first.isLfNode()){
			second.setLt(new Id_3());
			second.setRt(new Id_3());

			mkTrCp(first.getLt(), second.getLt());
			mkTrCp(first.getRt(), second.getRt());

		}
	}
	
	public List<Id_3> getLstLfNode(Id_3 rt){
		List<Id_3> lfLst = new ArrayList<>();
		if(rt.isLfNode()){ 
			lfLst.add(rt);
		}
		else{
			if(!rt.getLt().isLfNode()){
				getLstLfNode(rt.getLt());
			}
			if(!rt.getRt().isLfNode()){
				getLstLfNode(rt.getRt());
			}
		}
		return lfLst;
	}
	
	public void numLeafNodes(Id_3 root){		
		if(!root.isLfNode()){								
			yy++;
			root.setNodeNum(yy);
			numLeafNodes(root.getLt());
			numLeafNodes(root.getRt());
		}
	}
	
	public String calClass(Id_3 rt){
		int zeroCount = 0;
		int oneCount = 0;
		String majorClass = "0";
		List<Id_3> leafNodes = getLstLfNode(rt);
		for(Id_3 node : leafNodes){
			if(node.getValue().equalsIgnoreCase("1")){
				oneCount++;
			}
			else{
				zeroCount++;
			}
		}
		if(oneCount>zeroCount){
			majorClass = "1";
		}

		return majorClass;
	}
	
	public void replace(Id_3 root, int K){
		if(!root.isLfNode()){
			if(root.getNodeNum() == K){
		
				String leafChange = calClass(root);
				root.setLfNode(Boolean.TRUE);
				root.setLt(null);
				root.setRt(null);
				root.setValue(leafChange);
			}
			else{
				replace(root.getLt(), K);
				replace(root.getRt(), K);
			}

		}
	}
	
	public int getNonLeafNodes() {		
		int number = yy;
		setNonLeafNodes(0);
		return number;
	}
	
	public void setNonLeafNodes(int noNonLeafNodes) {
		this.yy = noNonLeafNodes;
	}
	
	public Id_3 prunedTree(Id_3 root, int x, int y, ArrayList<ArrayList<String>> Data1){
		Id_3 bestTree;
		Id_3 primeTree;
		bestTree = new Id_3();
		mkTrCp(root, bestTree);
		
		double accuracy = getAccuracy(bestTree, Data1);
		primeTree = new Id_3();
		for(int i=1; i<=x;i++){
			mkTrCp(root, primeTree);
			
			Random ran = new Random();

			int M = 1 + ran.nextInt(y);
			for(int j=0; j<=M; j++){
				numLeafNodes(primeTree);			
				int N = getNonLeafNodes();
				
				if(N>1){
					int P = ran.nextInt(N) + 1;
					replace(primeTree, P);
				}
				else{
					break;
				}
			}
			double accuracy1 = getAccuracy(primeTree, Data1);
			if (accuracy1 > accuracy){
				accuracy = accuracy1;
				mkTrCp(primeTree, bestTree);				
			}
		}
		return bestTree;
	}

	public boolean chkOp(Id_3 root, ArrayList<String> rw, ArrayList<String> attrList){
		Id_3 nodeCp = root;
		while(true){
			if(nodeCp.isLfNode()){
				if(nodeCp.getValue().equalsIgnoreCase(rw.get(rw.size()-1))){
					return true;
				}
				else{
					return false;
				}
			}

			int ind = attrList.indexOf(nodeCp.getName());
			String value = rw.get(ind);
			if(value.equalsIgnoreCase("0")){
				nodeCp = nodeCp.getLt();
			}
			else{
				nodeCp = nodeCp.getRt();
			}
		}
	}

	public double getAccuracy(Id_3 node, ArrayList<ArrayList<String>> data){
		double acc = 0;
		int oneExamples = 0;

		ArrayList<String> attr = data.get(0);
		for(ArrayList<String> rw : data.subList(1, data.size())){	
			boolean exampleCheck = chkOp(node, rw, attr);					
			if(exampleCheck){
				oneExamples++;
			}
		}
		acc = (((double) oneExamples / (double) (data.size()-1)) * 100.00);

		return acc;
	}
}


public class Id3main {

	public static void main(String[] args) {
		
			boolean printCnt;
			
			int X = Integer.parseInt(args[0]);
			int Y = 5;
			String trainingFile = args[1];
			String validationFile = args[2];
			String testFile = args[3];
			String print = args[4];

			if (print.equals("1")) {
				printCnt = Boolean.TRUE;
			} else {
				printCnt = Boolean.FALSE;
			}

			Id3main tst = new Id3main();
			ReadFile read = tst.new ReadFile();
			Id_tree tree = new Id_tree();

			try {

				ArrayList<ArrayList<String>> dataTraining = read.read(trainingFile);
				ArrayList<ArrayList<String>> dataValidation = read.read(validationFile);
				ArrayList<ArrayList<String>> dataTest = read.read(testFile);

				ArrayList<String> atrList = dataTraining.get(0);
				boolean flag = false;

				Id_3 treeGain = tree.treeBuild(dataTraining, atrList, flag);

				if (printCnt) {
					System.out.println("Tree Before Pruning");
					System.out.println();
					treeGain.print();
					System.out.println();
					System.out.println();
				}

				System.out.println("Accuracy of Tree before pruning : "
						+ tree.getAccuracy(treeGain, dataTest));

				System.out.println();

				Id_3 prunedTree = tree.prunedTree(treeGain, X, Y, dataValidation);

				if (printCnt) {
					System.out.println("Pruned Tree");
					System.out.println();
					treeGain.print();
					System.out.println();
					System.out.println();
				}

				System.out.println("Accuracy of Pruned Tree: "
						+ tree.getAccuracy(prunedTree, dataTest));

				System.out.println();

				

			} catch (IOException e) {
				System.out.println("File not found");

			}
		

	}
	public class ReadFile {


		public ArrayList<ArrayList<String>> read(String fName) throws IOException{

			ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
			File fl = new File(fName);
			Scanner ip;
			ip = new Scanner(fl);
			while(ip.hasNext()){
				String[] eachRow = ip.next().split(",");
				data.add(new ArrayList<String>(Arrays.asList(eachRow)));

			}
			ip.close();
			return data;
		}
	}
}


	
class gainCal {

	HashMap<String, ArrayList<String>> dataMap;
	HashMap<String, Double> gainMap ;

	public static double entropy(double pos, double neg){
		double total = pos + neg;
		double posProbability = pos/total;
		double negProbability = neg/total;

		if(pos == neg){
			return 1;
		}
		else if(pos == 0 || neg == 0){
			return 0;
		}
		else{
			double entropy = ((-posProbability) * (Math.log(posProbability)/Math.log(2))) + ((-negProbability)*(Math.log(negProbability)/Math.log(2)));
			return entropy;
		}

	}
	
	public static double var(double ones, double zeroes){
		double tot = ones + zeroes;
		double onesProb = ones/tot;
		double zeroesProb = zeroes/tot;

		if(ones == zeroes){
			return 1;
		}
		else if(ones == 0 || zeroes == 0){
			return 0;
		}
		else{
			double variance = ((onesProb) * (zeroesProb));
			return variance;
		}

	}
	
	public double varGain(double rootOnes, double rootZeroes, double oneLeft, double zeroLeft, double oneRight, double zeroRight){
		double totRoot = rootOnes + rootZeroes;
		double rootVar = var(rootOnes, rootZeroes);
		double leftVar = var(oneLeft,zeroLeft);
		double rightVar = var(oneRight, zeroRight);
		double totalLeft = oneLeft + zeroLeft;
		double totalRight = oneRight + zeroRight;

		double gain = rootVar - (((totalLeft/totRoot)* leftVar) + ((totalRight/totRoot) * rightVar));

		return gain;
	}
	
	public double infoGain(double rootOnes, double rootZeroes, double oneLeft, double zeroLeft, double oneRight, double zeroRight){
		double totRoot = rootOnes + rootZeroes;
		double rootEntr = entropy(rootOnes, rootZeroes);
		double leftEntr = entropy(oneLeft,zeroLeft);
		double rightEntr = entropy(oneRight, zeroRight);
		double totalLeft = oneLeft + zeroLeft;
		double totalRight = oneRight + zeroRight;

		double gain = rootEntr - (((totalLeft/totRoot)* leftEntr) + ((totalRight/totRoot) * rightEntr));

		return gain;
	}
	
	public static HashMap<String, ArrayList<String>> mapPopulate(ArrayList<ArrayList<String>> data) throws FileNotFoundException{
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		ArrayList<String> heads = data.get(0);	

		for(int i=0;i<heads.size();i++){
			for(int j=1;j<data.size();j++){
				if (map.containsKey(heads.get(i))){
					map.get(heads.get(i)).add(data.get(j).get(i));
				}
				else{
					ArrayList<String> values = new ArrayList<String>();
					values.add(data.get(j).get(i));
					map.put(heads.get(i), values);
				}
			}
		}
		return map;
	}
	
	public static HashMap<String,ArrayList<ArrayList<String>>> mapBestAttr(ArrayList<ArrayList<String>> data, String bestAttr){
		HashMap<String, ArrayList<ArrayList<String>>> reduceMap = new HashMap<String, ArrayList<ArrayList<String>>>();
		int ind = data.get(0).indexOf(bestAttr);
		
		for(int i=1;i<data.size();i++){
			if(data.get(i).get(ind).equalsIgnoreCase("0")){
				if(reduceMap.containsKey("0")){
					reduceMap.get("0").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reduceMap.put("0",dataAdd);
				}

			}
			else{
				if(reduceMap.containsKey("1")){
					reduceMap.get("1").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reduceMap.put("1",dataAdd);
				}
			}
		}

		return reduceMap;
	}


	public String attributeSelec(ArrayList<ArrayList<String>> data, ArrayList<String> attributeList,boolean flag) throws FileNotFoundException{
		String bestAttributes = "";
		dataMap = mapPopulate(data);
		gainMap = new HashMap<String, Double>();
		
		double classOnes = 0;
		double classZeroes = 0;
		for(String value : dataMap.get("Class")){
			if(value.equalsIgnoreCase("1")){
				classOnes++;
			}
			else{
				classZeroes++;
			}
		}

		for(String key: attributeList.subList(0, attributeList.size()-1)){		
			ArrayList<String> temp = dataMap.get(key);
			double onesLeft = 0;
			double onesRight = 0;
			double zeroesLeft = 0;
			double zeroesRight = 0;
			for(int i=0; i<temp.size();i++){								
				if(temp.get(i).equalsIgnoreCase("0")){
					if(dataMap.get("Class").get(i).equalsIgnoreCase("1")){
						onesLeft++;
					}
					else{
						zeroesLeft++;
					}
				}
				else{
					if(dataMap.get("Class").get(i).equalsIgnoreCase("1")){
						onesRight++;
					}
					else{
						zeroesRight++;
					}
				}
			}

			if (flag){
				Double gainForEachHead = varGain(classOnes, classZeroes, onesLeft, zeroesLeft, onesRight, zeroesRight);
				gainMap.put(key, gainForEachHead);
			} else {
				Double gainForEachHead = infoGain(classOnes, classZeroes, onesLeft, zeroesLeft, onesRight, zeroesRight);
				gainMap.put(key, gainForEachHead);
				
			}
		}

		ArrayList<Double> valList = new ArrayList<Double>(gainMap.values());
		Collections.sort(valList);
		Collections.reverse(valList);
		for(String key: gainMap.keySet()){
			if (valList.get(0).equals(gainMap.get(key))){
				bestAttributes = key;
				break;
			}
		}
		return bestAttributes;		
	}

}
