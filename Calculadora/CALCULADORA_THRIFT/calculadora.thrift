service Calculadora{
   void ping(),
   i32 suma(1:i32 num1, 2:i32 num2),
   i32 resta(1:i32 num1, 2:i32 num2),
   i32 producto(1:i32 num1, 2:i32 num2),
   double division(1:i32 num1, 2:i32 num2),
   list<i32> sumaVectores(1: list<i32> vector1, 2:list<i32> vector2),
   list<i32> restaVectores(1: list<i32> vector1, 2:list<i32> vector2),
   list<i32> productoVectores(1: list<i32> vector1, 2:list<i32> vector2),
   list<double> divisionVectores(1: list<i32> vector1, 2:list<i32> vector2),
   i32 productoEscalar(1: list<i32> vector1, 2:list<i32> vector2),
   list<list<i32>> sumaMatrices (1: list<list<i32>> matriz1, 2: list<list<i32>> matriz2),
   list<list<i32>> restaMatrices (1: list<list<i32>> matriz1, 2: list<list<i32>> matriz2),
   list<list<i32>> productoMatrices (1: list<list<i32>> matriz1, 2: list<list<i32>> matriz2)
}
