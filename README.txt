README.txt: Quick guide to coding up your strategy
Written by Frankie Lam in Spring 2020 / Edited by Betsy Pu Spring 2021

For this assignment you have to implement the interface Party.java and create a file
named "Party_netid.java" containing your strategy.

A brief tour of what each file does:

Redistricting.java: Implements I-cut-you-freeze algorithms
RedistrictingConfig.java: Initialises meta-variables d, N, T.
Party_*.java - Sample strategies
Party.java - the interface that your "Student_netid.java" must implement.
Tournament.java - A tournament infrastucture that will be the same across Strategy Designs.
parties.txt - A list of all the contesting strategies that are applying. This is so that you 
can test your strategies against each other and the sample strategies. 
Block.java - Helper functions.

Similarly, you do not have to understand or modify any of these files except Party.java and parties.txt for testing.

To start, read Party.java. You have to implement functions cut(..), choose(..), and accept(..). Remember to overwrite
the existig New(..) function with any initialisations. Next, check out the helper functions already written in Block.java,
especially the Comparator class. 

There are 3 sample strategies provided to help you get started. Feel free to use them as templates for your own strategy.

Your functions must terminate within 250 ms for N = 560, otherwise a "too long to construct/cut/choose/accept" exception
will be thrown. You can test this as usual via editing "parties.txt" and calling "make test".

Test your strategy against others by uploading it to the leadboard via the leaderboard tigerfile link 
(as opposed to the submission link). As a reminder, this is for your reference only - grading will 
be independent of the leaderboard!

Do not submit non-compiling solutions to the leaderboard-- it breaks the leaderboard and your course staff & classmates
will be sad. The most common compilation error to watch out for is when your filename does not match your class name.

After you submit your code, remember to use the "check your submission" button to make sure your code compiles, and that
everything makes sense to you.

Have fun!

