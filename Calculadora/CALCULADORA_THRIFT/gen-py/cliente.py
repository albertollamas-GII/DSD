import sys

from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

transport = TSocket.TSocket("localhost", 9090)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)

client = Calculadora.Client(protocol)

transport.open()

def basico (num1, operacion, num2):
    if (operacion == '+'):
        resultado = client.suma(num1,num2)
    elif(operacion == '-'):
        resultado = client.resta(num1, num2)
    elif (operacion == '*'):
        resultado = client.producto(num1, num2)
    elif (operacion == '/'):
        resultado = client.division(num1, num2)
    else:
        resultado = None
    
    return resultado

def vectores(vector1, vector2, operacion):
    if (len(vector1) == len(vector2)):
        if (operacion == '+'):
            resultado = client.sumaVectores(vector1,vector2)
        elif(operacion == '-'):
            resultado = client.restaVectores(vector1,vector2)
        elif (operacion == '*'):
            resultado = client.productoVectores(vector1,vector2)
        elif (operacion == '/'):
            resultado = client.divisionVectores(vector1,vector2)
        elif (operacion == 'escalar'):
            resultado = client.productoEscalar(vector1, vector2)
        else:
            resultado = None
    else:
        resultado = None
    
    return resultado

def matrices(matriz1, matriz2, operacion):
    if (len(matriz1) == len(matriz2)):
        if (operacion == '+'):
            resultado = client.sumaMatrices(matriz1,matriz2)
        elif(operacion == '-'):
            resultado = client.restaMatrices(matriz1,matriz2)
        elif (operacion == '*'):
            resultado = client.productoMatrices(matriz1,matriz2)
        else:
            resultado = None
    else:
        resultado = None
    
    return resultado

# Para introducir los operandos y operador, utilizaré sys (bibliografía usada en pdf)

argc = len(sys.argv)
argv = sys.argv

if (argc < 2):
    print ("\nUso: {argv[0]} <'vector'> <Num1/Vector1> <Operacion(+,-,x,/)> <Num2/Vector2>")
    exit(-1)
else: 
    print("hacemos ping al server")
    client.ping()

    if (argc == 2 and argv[1] == "vector"):
        vector1 = []
        n = int(input("Introduzca el numero de elementos de los vectores: "))
        print("Intrduzca los elementos del primer vector: ")
        for i in range (0,n):
            num = int(input())
            vector1.append(num)
        print (f"vector 1: {vector1}")

        operacion = input("Introduzca la operacion a realizar: " )

        vector2 = []
        print("Intrduzca los elementos del segundo vector: ")
        for i in range (0,n):
            num = int(input())
            vector2.append(num)
        print (f"vector 2: {vector2}")
        
        resultado = vectores(vector1, vector2, operacion)

        if (resultado != None):
            print(f"{vector1} {operacion} {vector2} = {resultado}")
        else:
            print("Error")
            exit(-1)
    elif (argc == 2 and argv[1] == "matriz"):
        
        filas = int(input("Introduzca el numero de filas de las matrices: "))
        columnas = int(input("Introduzca el numero de columnas de las matrices: "))

        matriz1 = []
        print("Introduzca los elementos de la primera matriz: ")
        for i in range (filas):
            a = []
            for j in range (columnas):
                a.append(int(input()))
            matriz1.append(a)
        
        for i in range (filas):
            for j in range (columnas):
                print(matriz1[i][j], end = " ")
            print()
        
        operacion = input("Introduzca la operacion a realizar: " )

        matriz2 = []
        print("Introduzca los elementos de la segunda matriz: ")
        for i in range (filas):
            a = []
            for j in range (columnas):
                a.append(int(input()))
            matriz2.append(a)

        for i in range (filas):
            for j in range (columnas):
                print(matriz2[i][j], end = " ")
            print()

        resultado = matrices(matriz1, matriz2, operacion)

        if (resultado != None):
            print(f"{matriz1} {operacion} {matriz2} = {resultado}")
        else:
            print("Error")
            exit(-1)
    elif(argc == 4):
        num1 = int (argv[1])
        operacion = argv[2][0]
        num2 = int (argv[3])

        resultado = basico(num1, operacion, num2)

        if (resultado != None):
            print(f"{num1} {operacion} {num2} = {resultado}")
        else:
            print("Error")
            exit(-1)

transport.close()
