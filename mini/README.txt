****************************
Alok Kumar Prusty (akp77)
Nikhil Anand Navali (nn259)
Prashanth Basappa (pb476)
*****************************

Execution:
Run the file MAPEvaluation.java with by setting following parameters in the program: 

variants is used to specify the strategy 
Line number 13 in  MAPEvaluation.java: static WEIGHTING variants = WEIGHTING.BM_25; 
This value can take "WEIGHTING.ATC_ATC", "WEIGHTING.ATN_ATN", "WEIGHTING.ANN_BPN", "WEIGHTING.BM_25" and "WEIGHTING.LTN_LTN"


The docType is used to specify the collection to be run on
Line number 14 in  MAPEvaluation.java: static DOCTYPE docType = DOCTYPE.MEDLAR;
This value can take "WEIGHTING.CACM", "WEIGHTING.MEDLAR"


Once these values are set, run the file and the MAP value for the particular strategy and collection is displayed.  