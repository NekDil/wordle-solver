# wordle-solver
Running (Works on macOS/Linux)
```
mvn compile exec:java -Dexec.mainClass="me.verma.app.WordleSolver"
```
It will recommend a word to start with, though you can still start with any word of your choice and enter it at prompt:
```
Feb 27, 2022 11:03:11 AM me.verma.app.WordleSolver getWordsOf5
INFO: Read 9972 words...
[house]
Enter current input:
```
Once you enter the word, it asks you for hint. Which is the hint you got from Wordle/Squabble. A typical hints looks like: `bgbby`.
Assuming the word is `power`. `b` means complete miss, `g` means exact match (green) and `y` means possition match (yellow).
When I enter this hint at prompt, it gives me my next possible moves.
```
INFO: Read 9972 words...
[house]
Enter current input:
house
Enter hint:
bgbby
Feb 27, 2022 11:07:40 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 4870 words after mismatch removal!
Feb 27, 2022 11:07:40 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 770 words after green inclusion!
Feb 27, 2022 11:07:40 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 258 words after yellow consideration!
[wolve, corke, coree, cored,
...
```
This way it keeps on narrowing down and in couple of iterations and good judgement you can solve almost all wordles.
```
Enter current input:
lover
Enter hint:
bgbgg
Feb 27, 2022 11:10:55 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 29 words after mismatch removal!
Feb 27, 2022 11:10:55 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 29 words after green inclusion!
Feb 27, 2022 11:10:55 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 29 words after yellow consideration!
[toner, mower, noter, joker, wooer, gomer, toper, roker, power, goner, roper, toter, poker, foyer, tower, moner, gorer, toyer, yoker, roter, moper, porer, moter, yomer, tozer, poter, roger, jower, rower]
Enter current input:
tower
Enter hint:
bgggg
Feb 27, 2022 11:11:32 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 19 words after mismatch removal!
Feb 27, 2022 11:11:32 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 4 words after green inclusion!
Feb 27, 2022 11:11:32 AM me.verma.app.WordleSolver guessBestNextWords
INFO: Remaining 4 words after yellow consideration!
[mower, power, jower, rower]
Enter current input:
```
