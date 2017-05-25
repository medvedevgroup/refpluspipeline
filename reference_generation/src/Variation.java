import java.util.StringTokenizer;


public class Variation {
	public static enum TYPE {INSERTION, DELETION, TRANSLOCATION_DELETION, INVERSION, TANDEM, TRANSLOCATION, INVERTED_TRANSLOCATION, DUPLICATION, INVERTED_DUPLICATION, INTERCHROMOSOMAL_TRANSLOCATION, INTERCHROMOSOMAL_DUPLICATION, INTERCHROMOSOMAL_INVERTED_TRANSLOCATION, INTERCHROMOSOMAL_INVERTED_DUPLICATION, SNP};
	
	private TYPE type;
	private String sequence;
	private GenomicInterval location;
	boolean isHomozygous;

	public Variation(){
		//nottin
	}
	public Variation(TYPE t, String s, GenomicInterval l){
		this.type = t;
		this.sequence = s;
		this.location = l;
	}
	public Variation(String bedLine){
		StringTokenizer t = new StringTokenizer(bedLine);
		String type = t.nextToken();
		this.type = TYPE.valueOf(type);
		StringTokenizer l = new StringTokenizer(t.nextToken(),":-");
			this.location = new GenomicInterval(l.nextToken(), new Interval(Integer.parseInt(l.nextToken()), Integer.parseInt(l.nextToken())));
		if(t.hasMoreTokens()){
			this.sequence = t.nextToken();
		}
	}
	public TYPE getType() {
		return type;
	}
	public String getSequence() {
		return sequence;
	}
	public GenomicInterval getLocation() {
		return location;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public void setLocation(GenomicInterval location) {
		this.location = location;
	}
	public boolean isHomozygous() {
		return isHomozygous;
	}
	public void setHomozygous(boolean isHomozygous) {
		this.isHomozygous = isHomozygous;
	}
	public GenomicInterval parseSequenceAsInterval() {
		if (!this.sequence.contains(":")){
			return null;	
		}
		StringTokenizer t = new StringTokenizer(this.sequence, ":-");
		String chr = t.nextToken();
		int st = Integer.parseInt(t.nextToken());
		int en = Integer.parseInt(t.nextToken());
		return new GenomicInterval(chr, new Interval(st, en));
	}
	@Override public String toString() {
		return (type+"\t"+location.toString()+"\t"+(sequence==null?"":sequence));
	}
}
