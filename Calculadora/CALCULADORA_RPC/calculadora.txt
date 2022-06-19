struct ecuacionCuadrada{
    double a;
    double b;
    double c;
    double res1;
    double res2;
};

typedef double vect<>;

program CALCULADORA{
    version CALCULADORAV1{
        double SUMA(double, double) = 1;
        double RESTA(double, double) = 2;
        double MULTIPLICACION(double, double) = 3;
        double DIVISION(double, double) = 4;
        ecuacionCuadrada CUADRATICA(double, double, double) = 5;
        vect SUMAVECTORES(vect,vect) = 6;
        vect RESTAVECTORES(vect,vect) = 7;
        vect PRODUCTOVECTORES(vect,vect) = 8;
        vect DIVISIONVECTORES(vect,vect) = 9;
        double PRODUCTOESCALAR(vect,vect) = 10;
    } = 1;
} = 0x20000001;
