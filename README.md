JVM Bindings for Expokit
========================

[Expokit] is software written in Fortran by Roger Sidje for
calculating various kinds of matrix exponentials of the 
form

   * exp(t*H) - Pade and Chebyshev approximations
   * exp(t*H)*v - Krylov subspace iteration
   * exp(t*H)*v + t*phi(t*H)*u - Exponential Euler solution
     of the initial value problem w' = Hw +u, w(0) = v, with
     phi(z) = z^1(exp(z)-1).

This software is part of the [Module Integration Simulator]
developed at the [University of Edinburgh School of Informatics].

Do build with sbt:

~~~~~
> compile
> javah
> native
> test
~~~~~

[Expokit]: http://www.maths.uq.edu.au/expokit/
[Module Integration Simulator]: https://edinburgh-rbm.github.io/
[University of Edinburgh School of Informatics]: http://www.inf.ed.ac.uk/
