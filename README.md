# Practical Category Theory

This repository contains examples and code related to category theory constructs in Scala. The aim is to build a better mental model on when/how can you apply Category theory concepts on everyday activities.

## On Category theory and programming

I add this small note here as it was quite enlightening on my approach to Cats and similar libraries.

Originally my train of thought was that I had to know all category theory well and then, somehow, I'd identify patterns that would help me in daily coding, ending up with the code structures that amazing FP devs showcase often.

I was wrong. Horribly so.

I wish I had not lost the source, as it deserves a quote and a sincere thanks, but I read something in Reddit (yup) which showed me why I was approaching this the wrong way. From memory, what the expert said was:

Create your own data types. Build the domain for your application. And think about its properties. Does it have a 'hole'? Is it commutative? Does it have an identity element? Etc.

Then, and only then, you can look at those properties and match them with known Category theory structures. If you have a match, congratulations, you just got a lot of stuff for free by creating the proper instance of a Typeclass. Otherwise, it's not a big deal.

The same applies to functions. Build what you need. Try to keep them generic (F[_] : Traversable instead of List[_], for example). And, once you have the signature, if you see it matches something generic and standard: great! you don't have to write the implementation. Otherwise, just write it.

Of course, with more experience you will start identifying ways to define types and functions that approach standard structures, and benefit more from this. But the aim is not to go from Category Theory to your data, but the other way around. 

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with
any pull requests, please state that the contribution is your original work and that you license
the work to the project under the project's open source license. Whether or not you state this
explicitly, by submitting any copyrighted material via pull request, email, or other means you
agree to license the material under the project's open source license and warrant that you have the
legal authority to do so.

## License ##

This code is open source software licensed under the
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0) license.
