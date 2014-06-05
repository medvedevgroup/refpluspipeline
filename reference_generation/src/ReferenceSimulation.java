import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;


import net.sf.picard.reference.FastaSequenceIndex;
import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;




public class ReferenceSimulation {
	
	public static Hashtable<String, Integer> fetchChromosomeLengths(String filename) throws IOException {
		Hashtable<String, Integer> out = new Hashtable<String, Integer>();
		
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line;
		while ( (line = in.readLine()) != null) {
			StringTokenizer t = new StringTokenizer(line);
			String n = t.nextToken();
			int len = Integer.parseInt(t.nextToken());
			out.put(n, len);
		}
		in.close();
		return out;
	}
	
	private static ArrayList<String> fetchSequenceNames(String filename) throws IOException {
		ArrayList<String> out = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line;
		while ( (line = in.readLine()) != null) {
			StringTokenizer t = new StringTokenizer(line);
			String n = t.nextToken();
			out.add(n);
		}
		
		in.close();
		return out;
	}
	
	public static String reverseComplement(String sequence) {
		StringBuilder rcSequence = new StringBuilder();
		for ( int i=sequence.length()-1; i>=0; --i ) {
			rcSequence.append( getNucleotideComplement(sequence.charAt(i)) );
		}
		return rcSequence.toString();
	}
	private static char getNucleotideComplement(char nt) {
		switch (nt) {
		case 'A': return 'T';
		case 'T': return 'A';
		case 'C': return 'G';
		case 'G': return 'C';
		case 'a': return 't';
		case 't': return 'a';
		case 'c': return 'g';
		case 'g': return 'c';
		default: return 'N';
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main1(String[] args) throws IOException {
		
		if(args.length < 3){
			System.err.println("Usage: <reference sequence> <target chromosome> <variation file>");
			System.exit(0);
		}
		
		String filename = args[0];
//		String filename = "/Users/schroeder/Documents/workspace/references/mm10_pUC12_karyotipic.fa";
		FastaSequenceIndex index = new FastaSequenceIndex(new File(filename+".fai"));
		
		IndexedFastaSequenceFile reference = new IndexedFastaSequenceFile(new File(filename), index);
		
		if(! reference.isIndexed()){
			System.err.println("Input reference is not indexed. Abort.");
			System.exit(1);
		}
		
//		String variationsFile = "examples/variations.bed";
		String variationsFile = args[2];
		BufferedReader inputVariations = new BufferedReader(new FileReader(variationsFile));
		
//		FileWriter out = new FileWriter("examples/out.fa");
		FileWriter out = new FileWriter(variationsFile + "_reference.fa");
		
		ArrayList<Variation> vars = new ArrayList<Variation>();
		String line;
		while(( line = inputVariations.readLine()) != null){
			vars.add(new Variation(line));
		}
		inputVariations.close();
		
//		Variation var = new Variation(Variation.TYPE.SNP, "A", new GenomicInterval("gi|310821|gb|L09129.1|SYNPUC12V", new Interval(2, 2)));
//		vars.add(var);
//		var = new Variation(Variation.TYPE.DELETION, null, new GenomicInterval("gi|310821|gb|L09129.1|SYNPUC12V", new Interval(10,19)));
//		vars.add(var);
//		var = new Variation(Variation.TYPE.INSERTION, "chr10:50-99", new GenomicInterval("gi|310821|gb|L09129.1|SYNPUC12V", new Interval(50, 50)));
//		vars.add(var);
		int lastLocation = 1;
		int newRefLocation = 1;
		String chromosome = args[1];
		
		out.write(">"+chromosome+"\n");
		
		FileWriter offsets = new FileWriter("offsets.txt");
		
		for(Variation v: vars){
			String seq = new String(reference.getSubsequenceAt(chromosome, lastLocation, v.getLocation().start-1).getBases());
			newRefLocation += seq.length();
			out.write(seq);

			switch(v.getType()){
			case DELETION: 
				lastLocation = v.getLocation().end+1;
				System.out.println("DELETION\t"+chromosome+":"+newRefLocation+"-"+newRefLocation);
				break;
			case INSERTION: 
			case TRANSLOCATION:
				GenomicInterval insert = v.parseSequenceAsInterval();
				String insertion;
				if (insert != null){
					insertion = new String(reference.getSubsequenceAt(insert.chrom, insert.start, insert.end).getBases());
				} else 
					insertion = v.getSequence();
				out.write(insertion);
				System.out.println(v.getType()+"\t"+chromosome+":"+newRefLocation+"-"+(newRefLocation+insertion.length()));
				newRefLocation += insertion.length();
				lastLocation = v.getLocation().end;
				break;
			case SNP: out.write(v.getSequence());
				System.out.println("SNP\t"+chromosome+":"+newRefLocation+"-"+newRefLocation);
				newRefLocation ++;
				lastLocation = v.getLocation().end + 1;
				break;
			case INVERSION: byte[] invert = reference.getSubsequenceAt(v.getLocation().chrom, v.getLocation().start, v.getLocation().end).getBases();
				ArrayUtils.reverse(invert);
				out.write(new String(invert));
				System.out.println("INVERSION\t"+chromosome+":"+newRefLocation+"-"+(newRefLocation+invert.length));
				newRefLocation += invert.length;
				lastLocation = v.getLocation().end + 1;
				break;
			default: System.err.println("Unknown Variation!");
			}
			offsets.write(newRefLocation+"\t"+(newRefLocation- lastLocation)+"\n");
		}
		
		
		
		
		Hashtable<String, Integer> chrLens = fetchChromosomeLengths(filename+".fai");
		
		int len = chrLens.get(chromosome);
		
		String r = new String(reference.getSubsequenceAt(chromosome, lastLocation, len).getBases());
		
		out.write(r);
		
		out.flush();
		out.close();
		
		offsets.flush();
		offsets.close();
	}




	public static void main(String[] args) throws IOException {

		if(args.length < 2){
			System.err.println("Usage: <reference sequence> <variation file>");
			System.exit(0);
		}

		String filename = args[0];
		FastaSequenceIndex index = new FastaSequenceIndex(new File(filename+".fai"));

		IndexedFastaSequenceFile reference = new IndexedFastaSequenceFile(new File(filename), index);

		if(! reference.isIndexed()){
			System.err.println("Input reference is not indexed. Abort.");
			System.exit(1);
		}

		String variationsFile = args[1];
		BufferedReader inputVariations = new BufferedReader(new FileReader(variationsFile));

		FileWriter out = new FileWriter(variationsFile + "_reference.fa");
		FileWriter offsets = new FileWriter(variationsFile + "_offsets.txt");
		FileWriter breakpoints = new FileWriter(variationsFile + "_breakpoints.txt");
		
		ArrayList<String> chromosomes = fetchSequenceNames(filename+".fai");
		Hashtable<String, Integer> chrLens = fetchChromosomeLengths(filename+".fai");

		Hashtable<String, ArrayList<Variation>> vars = new Hashtable<String, ArrayList<Variation>>();
		for(String chr: chromosomes){
			vars.put(chr, new ArrayList<Variation>());
		}
		String line;
		while(( line = inputVariations.readLine()) != null){
			Variation v = new Variation(line);
			ArrayList<Variation> l = vars.get(v.getLocation().chrom);
			l.add(v);
		}
		inputVariations.close();


		

		for(String chromosome: chromosomes){
			int lastLocation = 1;
			int newRefLocation = 1;
			

			out.write(">"+chromosome+"\n");
			ArrayList<Variation> l = vars.get(chromosome);
			
			for(Variation v: l){
				String seq = new String(reference.getSubsequenceAt(chromosome, lastLocation, v.getLocation().start-1).getBases());
				out.write(seq);
				newRefLocation += seq.length();
				
				switch(v.getType()){
				case DELETION: 
					lastLocation = v.getLocation().end+1;
					System.out.println("DELETION\t"+chromosome+":"+newRefLocation+"-"+newRefLocation);
					offsets.write(chromosome+"\t"+newRefLocation+"\t"+(newRefLocation - lastLocation)+"\n");
					breakpoints.write((v.getLocation().start-1)+"\t"+(v.getLocation().end+1)+"\n");
					break;
				case TANDEM:
					seq = new String(reference.getSubsequenceAt(chromosome, v.getLocation().start, v.getLocation().end).getBases());
					out.write(seq);
					out.write(seq);
					newRefLocation += 2*seq.length();
					lastLocation = v.getLocation().end;
					offsets.write(chromosome+"\t"+newRefLocation+"\t"+(newRefLocation - lastLocation)+"\n");
					breakpoints.write((v.getLocation().start)+"\t"+(v.getLocation().end)+"\n");
					break;
				case INSERTION: 
				case TRANSLOCATION:
					GenomicInterval insert = v.parseSequenceAsInterval();
					String insertion;
					if (insert != null){
						insertion = new String(reference.getSubsequenceAt(insert.chrom, insert.start, insert.end).getBases());
					} else 
						insertion = v.getSequence();
					out.write(insertion);
					System.out.println(v.getType()+"\t"+chromosome+":"+newRefLocation+"-"+(newRefLocation+insertion.length()));
					newRefLocation += insertion.length();
					lastLocation = v.getLocation().end;
					offsets.write(chromosome+"\t"+newRefLocation+"\t"+(newRefLocation - lastLocation)+"\n");
					//TODO: works only on same chr
					if(insert != null){
						breakpoints.write((v.getLocation().start)+"\t"+(insert.start)+"\n");
						breakpoints.write((v.getLocation().end+1)+"\t"+(insert.end)+"\n");
					} else {
						breakpoints.write((v.getLocation().start)+"\t"+(v.getLocation().end+1)+"\n");
					}
					break;
				case SNP: out.write(v.getSequence());
					System.out.println("SNP\t"+chromosome+":"+newRefLocation+"-"+newRefLocation);
					newRefLocation ++;
					lastLocation = v.getLocation().end + 1;
					offsets.write(chromosome+"\t"+newRefLocation+"\t"+(newRefLocation - lastLocation)+"\n");
					break;
				case INVERSION: String invert = reverseComplement( new String(reference.getSubsequenceAt(v.getLocation().chrom, v.getLocation().start, v.getLocation().end).getBases()));
					out.write(new String(invert));
					System.out.println("INVERSION\t"+chromosome+":"+newRefLocation+"-"+(newRefLocation+invert.length()));
					newRefLocation += invert.length();
					lastLocation = v.getLocation().end + 1;
					offsets.write(chromosome+"\t"+newRefLocation+"\t"+(newRefLocation - lastLocation)+"\n");
					breakpoints.write((v.getLocation().start-1)+"\t"+(v.getLocation().end)+"\n");
					breakpoints.write((v.getLocation().start)+"\t"+(v.getLocation().end+1)+"\n");
					break;
				default: System.err.println("Unknown Variation!");
				}
			}

			int len = chrLens.get(chromosome);

			String r = new String(reference.getSubsequenceAt(chromosome, lastLocation, len).getBases());

			out.write(r);
			out.write("\n");

			out.flush();
		}

		offsets.flush();
		offsets.close();
		breakpoints.flush();
		breakpoints.close();
		out.close();

	}

}