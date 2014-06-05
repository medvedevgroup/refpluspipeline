INSTRUCTION MANUAL

1. Create the new reference (ref+), which includes the structural variations of interest. See README.txt in reference_geneneration for details.

2. Index the new reference, and align reads to it with a mapper of choice (bwa, bowtie2, ...) -- the mapper is best chosen depending on the SV detection tools used in the enxt step.

3. Run an SV detection tool on the aligned read set (Delly, BreakDancer, Socrates). The choice here is dependent on the properties of the sequencing library: soft-clip based tools such as Socrates require long reads and deep coverage; pure paired-end methods such as BreakDancer rely on high average insert size and a tight distribution for maximum effct. Multiple tools can be run at this step if desired.

4. Evaluate the results. To assess the presence or absence of the variants added to the novel reference, the output from the SV detection has to be compared to the list of variants. A simple overlapping tool in results_evaluation takes two bed files and establishes the recall, precision, and f-score of the best overlap of intervals in the first bed file and any interval in the second bed file. See the README.txt in the results_evaluation-directory for further instructions.

