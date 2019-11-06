package LA04;

import java.util.concurrent.atomic.DoubleAccumulator;

// ===========================================================================
class Acumula {
	// ===========================================================================
	double  suma;

	// -------------------------------------------------------------------------
	Acumula() {
		this.suma = 0;
	}

	// -------------------------------------------------------------------------
	synchronized void acumulaDato( double dato ) {
		suma += dato;
	}

	// -------------------------------------------------------------------------
	synchronized double dameDato() {
		return suma;
	}
}

// ===========================================================================
class MiHebraMultAcumulaciones1a extends Thread {
	// ===========================================================================
	int      miId, numHebras;
	long     numRectangulos;
	Acumula  a;

	// -------------------------------------------------------------------------
	MiHebraMultAcumulaciones1a(int miId, int numHebras, long numRectangulos, Acumula a ) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.numRectangulos = numRectangulos;
		this.a = a;
	}

	// -------------------------------------------------------------------------
	public void run() {
		double baseRectangulo = 1.0 / (double)numRectangulos;
		for(int i = miId; i < numRectangulos; i+=numHebras) {
			double x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			a.acumulaDato(4.0/( 1.0 + x*x));
		}
	}
}

// ===========================================================================
class MiHebraUnaAcumulacion1b extends Thread {
	// ===========================================================================
	int      miId, numHebras;
	long     numRectangulos;
	Acumula  a;

	// -------------------------------------------------------------------------
	MiHebraUnaAcumulacion1b(int miId, int numHebras, long numRectangulos, Acumula a ) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.numRectangulos = numRectangulos;
		this.a = a;
	}

	// -------------------------------------------------------------------------
	public void run() {
		double baseRectangulo = 1.0 / (double)numRectangulos;
		double acumulado = 0;
		for(int i = miId; i < numRectangulos; i+=numHebras) {
			double x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			acumulado += (4.0/( 1.0 + x*x));
		}
		a.acumulaDato(acumulado);
	}
}

// ===========================================================================
class MiHebraMultAcumulacionesAtomicas1c extends Thread {
	// ===========================================================================
	int      miId, numHebras;
	long     numRectangulos;
	DoubleAccumulator a;

	// -------------------------------------------------------------------------
	MiHebraMultAcumulacionesAtomicas1c(int miId, int numHebras, long numRectangulos, DoubleAccumulator a ) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.numRectangulos = numRectangulos;
		this.a = a;
	}

	// -------------------------------------------------------------------------
	public void run() {
		double baseRectangulo = 1.0 / (double)numRectangulos;
		for(int i = miId; i < numRectangulos; i+=numHebras) {
			double x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			a.accumulate(4.0/( 1.0 + x*x));
		}
	}
}

// ===========================================================================
class MiHebraUnaAcumulacionAtomica1d extends Thread {
	// ===========================================================================
	int      miId, numHebras;
	long     numRectangulos;
	DoubleAccumulator a;

	// -------------------------------------------------------------------------
	MiHebraUnaAcumulacionAtomica1d(int miId, int numHebras, long numRectangulos, DoubleAccumulator a ) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.numRectangulos = numRectangulos;
		this.a = a;
	}

	// -------------------------------------------------------------------------
	public void run() {
		double baseRectangulo = 1.0 / (double)numRectangulos;
		double acumulado = 0;
		for(int i = miId; i < numRectangulos; i+=numHebras) {
			double x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			acumulado += (4.0/( 1.0 + x*x));
		}
		a.accumulate(acumulado);
	}
}

// ===========================================================================
class EjemploNumeroPI1a {
// ===========================================================================

	// -------------------------------------------------------------------------
	public static void main( String args[] ) {
		long                        numRectangulos;
		double                      baseRectangulo, x, suma, pi;
		int                         numHebras;
		MiHebraMultAcumulaciones1a vt[];
		Acumula                     a;
		long                        t1, t2;
		double                      tSec, tPar;

		// Comprobacion de los argumentos de entrada.
		if( args.length != 2 ) {
			System.out.println( "ERROR: numero de argumentos incorrecto.");
			System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
			System.exit( -1 );
		}
		try {
			numHebras      = Integer.parseInt( args[ 0 ] );
			numRectangulos = Long.parseLong( args[ 1 ] );
		} catch( NumberFormatException ex ) {
			numHebras      = -1;
			numRectangulos = -1;
			System.out.println( "ERROR: Numeros de entrada incorrectos." );
			System.exit( -1 );
		}

		System.out.println();
		System.out.println( "Calculo del numero PI mediante integracion." );

		//
		// Calculo del numero PI de forma secuencial.
		//
		System.out.println();
		System.out.println( "Comienzo del calculo secuencial." );
		t1 = System.nanoTime();
		baseRectangulo = 1.0 / ( ( double ) numRectangulos );
		suma           = 0.0;
		for( long i = 0; i < numRectangulos; i++ ) {
			x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			suma += f( x );
		}
		pi = baseRectangulo * suma;
		t2 = System.nanoTime();
		tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Version secuencial. Numero PI: " + pi );
		System.out.println( "Tiempo secuencial (s.):        " + tSec );

		//
		// Calculo del numero PI de forma paralela:
		// Multiples acumulaciones por hebra.
		//
		System.out.println();
		System.out.print( "Comienzo del calculo paralelo: " );
		System.out.println( "Multiples acumulaciones por hebra." );

		t1 = System.nanoTime();

		MiHebraMultAcumulaciones1a ha[] = new MiHebraMultAcumulaciones1a[numHebras];
		Acumula acumulaA = new Acumula();
		for(int i = 0; i < numHebras; i++) {
			ha[i] = new MiHebraMultAcumulaciones1a(i, numHebras, numRectangulos, acumulaA);
			ha[i].start();
		}
		for(int i = 0; i < numHebras; i++) {
			try {
				ha[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		t2 = System.nanoTime();

		pi = baseRectangulo * acumulaA.dameDato();
		tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Calculo del numero PI:   " + pi );
		System.out.println( "Tiempo ejecucion (s.):   " + tPar );
		System.out.println( "Incremento velocidad :   " + tSec/tPar );

		//
		// Calculo del numero PI de forma paralela:
		// Una acumulacion por hebra.
		//
		System.out.println();
		System.out.print( "Comienzo del calculo paralelo: " );
		System.out.println( "Una acumulacion por hebra." );

		t1 = System.nanoTime();

		MiHebraUnaAcumulacion1b hb[] = new MiHebraUnaAcumulacion1b[numHebras];
		Acumula acumulaB = new Acumula();
		for(int i = 0; i < numHebras; i++) {
			hb[i] = new MiHebraUnaAcumulacion1b(i, numHebras, numRectangulos, acumulaB);
			hb[i].start();
		}
		for(int i = 0; i < numHebras; i++) {
			try {
				hb[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		t2 = System.nanoTime();

		pi = baseRectangulo * acumulaB.dameDato();
		tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Calculo del numero PI:   " + pi );
		System.out.println( "Tiempo ejecucion (s.):   " + tPar );
		System.out.println( "Incremento velocidad :   " + tSec/tPar );

		//
		// Calculo del numero PI de forma paralela:
		// Multiples acumulaciones por hebra (Atomica)
		//
		System.out.println();
		System.out.print( "Comienzo del calculo paralelo: " );
		System.out.println( "Multiples acumulaciones por hebra (At)." );

		t1 = System.nanoTime();

		MiHebraMultAcumulacionesAtomicas1c hc[] = new MiHebraMultAcumulacionesAtomicas1c[numHebras];
		DoubleAccumulator acumulaC = new DoubleAccumulator((curV, newV) -> curV+newV, 0.0);
		for(int i = 0; i < numHebras; i++) {
			hc[i] = new MiHebraMultAcumulacionesAtomicas1c(i, numHebras, numRectangulos, acumulaC);
			hc[i].start();
		}
		for(int i = 0; i < numHebras; i++) {
			try {
				hc[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		t2 = System.nanoTime();

		pi = baseRectangulo * acumulaC.get();
		tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Calculo del numero PI:   " + pi );
		System.out.println( "Tiempo ejecucion (s.):   " + tPar );
		System.out.println( "Incremento velocidad :   " + tSec/tPar );

		//
		// Calculo del numero PI de forma paralela:
		// Una acumulacion por hebra (Atomica).
		//
		System.out.println();
		System.out.print( "Comienzo del calculo paralelo: " );
		System.out.println( "Una acumulacion por hebra (At)." );

		t1 = System.nanoTime();

		MiHebraUnaAcumulacionAtomica1d hd[] = new MiHebraUnaAcumulacionAtomica1d[numHebras];
		DoubleAccumulator acumulaD = new DoubleAccumulator((curV, newV) -> curV+newV, 0.0);
		for(int i = 0; i < numHebras; i++) {
			hd[i] = new MiHebraUnaAcumulacionAtomica1d(i, numHebras, numRectangulos, acumulaD);
			hd[i].start();
		}
		for(int i = 0; i < numHebras; i++) {
			try {
				hd[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		t2 = System.nanoTime();

		pi = baseRectangulo * acumulaD.get();
		tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Calculo del numero PI:   " + pi );
		System.out.println( "Tiempo ejecucion (s.):   " + tPar );
		System.out.println( "Incremento velocidad :   " + tSec/tPar );

		System.out.println();
		System.out.println( "Fin de programa." );
	}

	// -------------------------------------------------------------------------
	static double f( double x ) {
		return ( 4.0/( 1.0 + x*x ) );
	}
}

