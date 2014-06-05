The interval_overlap_deletions.py script takes two files as an input. In the 
context of the ref+ pipeline, these files should be the list of variants and 
the (parsed) output of the SV caller. Both files are expected to be in standard 
tab- or whitespace-delimited format with each row containing the reference 
chromosome, the starting position, and the end position. Neither of the files 
have to be sorted, but can be arranged in any order. This makes the script 
slow and inefficient, but relieves some of the strains of pre-processing. 
The output of the script contains the variation coordinates, followed by three
evaluation metrics, and the line of output from the SV caller. The metrics are
the recall, the precision, and the f-score for the variant from file1 and the
best hit from file2. If there are no overlapping intervals for a variant in
file1, the metrics are set to 0. Otherwise, recall describes the proportion of
the variant being overlapped by a call in file2, precision the proportion of
the overlapping SV call, and F-score the combination of the two
(2*recall*precision)/(recall+precision).

