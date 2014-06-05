/**
 * 
 */

import java.io.Serializable;
/**
 * @author Arthur Hsu
 * Created: Sep 24, 2012
 *
 */
public class GenomicInterval extends Interval implements Serializable {
	static final long serialVersionUID = 61393454553L; 

	public String chrom;
	private char strand;
	
	/**
	 * Constructor. Defaults to CLOSED_INTERVAL type and unstranded interval.
	 * @param chrom
	 * @param start
	 * @param end
	 */
	public GenomicInterval(String chrom, int start, int end) {
		this(chrom, start, end, '.');
	}
	
	/**
	 * Constructor. Defaults to CLOSED_INTERVAL type and unstranded interval.
	 * @param chrom
	 * @param coord
	 */
	public GenomicInterval(String chrom, Interval coord) {
		this(chrom, coord.start, coord.end, '.');
	}
	
	/**
 	 * Constructor. Defaults to CLOSED_INTERVAL type, but with specified strand (NOTE. start and end will be reordered to have start < end).
	 * @param chrom
	 * @param start
	 * @param end
	 * @param strand
	 */
	public GenomicInterval(String chrom, int start, int end, char strand) {
		this(chrom, start, end, strand, Interval.INTERVAL_TYPE.CLOSED_INTERVAL);
	}

	/**
	 * Constructor. Specifying all parameters.
	 * @param chrom
	 * @param start
	 * @param end
	 * @param strand
	 * @param type
	 */
	public GenomicInterval(String chrom, int start, int end, char strand, Interval.INTERVAL_TYPE type) {
		this.chrom = chrom;
		int s = (start<0 || end<0) ? Math.max(start, end) : Math.min(start,end);
		int e = (start<0 || end<0) ? s : Math.max(start, end);
		this.start = s;
		this.end = e;
		Interval.type = type;
		
		if (strand==' ') { // default is to have strand determined by coordinate
			this.strand = (s!=start) ? '-' : '+';
		} else {
			this.strand = (strand=='+' || strand=='-') ? strand : '.';
		}
	}
	
	/**
	 * Create a new instance of GenomicInterval that is shifted from this one by "offset" (i.e. negative number to move upstream).
	 * @param offset
	 * @return
	 */
	public GenomicInterval createShiftedInterval(int offset) {
		return new GenomicInterval( chrom, Math.max(1, start+offset), end+offset );
	}
	
	/**
	 * Provide ordering of two GenomicInterval classes. First order by chromosome (lexigraphically), then by coordinate. 
	 */
	public int compareTo(GenomicInterval other) {
		int r = this.chrom.compareTo(other.chrom);
		return (r!=0) ? r : super.compareTo(other);
	}
	
	/***
	 * Tests if the genomic interval intersects another.
	 * @param other
	 * @return
	 */
	public boolean intersects(GenomicInterval other) {
		return this.intersects(other, 0);
	}
	
	/**
	 * Tests if the genomic interval fully contains the other.
	 * @param other
	 * @return
	 */
	public boolean contains(GenomicInterval other) {
		return this.contains(other, 0);
	}

	/**
	 * Tests if the genomic interval intersects another with a flanking region. 
	 * @param other
	 * @param flank
	 * @return
	 */
	public boolean intersects(GenomicInterval other, int flank) {
		if (!this.chrom.equals(other.chrom)) return false;
		if (this.strand != '.' && this.strand != other.strand) return false;
		return super.intersects(other, flank);
	}
	
	/**
	 * Tests if the genomic interval fully contains the other with a flanking region.
	 * @param other
	 * @param flank
	 * @return
	 */
	public boolean contains(GenomicInterval other, int flank) {
		if (!this.chrom.equals(other.chrom)) return false;
		if (this.strand != '.' && this.strand != other.strand) return false;
		return super.contains(other, flank);
	}
	
	
	@Override
	public String toString() {
		switch (strand) {
		case '-':
			return chrom + ":" + start + "-" + end + " " + "rev";
		case '+':
			return chrom + ":" + start + "-" + end + " " + "fwd";
		default:
			return chrom + ":" + start + "-" + end;
		}
	}
}