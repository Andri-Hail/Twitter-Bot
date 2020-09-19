# Twitter-Bot
This is a program that uses real twitter data as a training set to construct its own tweets. First the training tweets are 
"cleaned" and parsed into individual sentences. Then a Markov Chain is used to track the frequency that one value is followed 
by another. Finally, this information is used to generate tweets where every successive word is picked based on the probability 
that it follows the previous one. 
