package LA03;

// ============================================================================
class MiHebraCiclica extends Thread {
// ============================================================================
	int miId;
	int numHebras;
	long vectorNumeros[];

	// -------------------------------------------------------------------------
	MiHebraCiclica (int miId, int numHebras, long vectorNumeros[]) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.vectorNumeros = vectorNumeros;
	}

	public void run() {
		for(int i = miId; i < vectorNumeros.length; i+=numHebras) {
			if( EjemploMuestraPrimosEnVector2b.esPrimo( vectorNumeros[i] ) ) {
				System.out.println( "  Encontrado primo: " + vectorNumeros[i] );
			}
		}
	}
}

// ===========================================================================
public class EjemploMuestraPrimosEnVector2b {
// ===========================================================================

	// -------------------------------------------------------------------------
	public static void main( String args[] ) {
		int     numHebras;
		long    t1, t2;
		double  ts, tc, tb, td;
		long    vectorNumeros[] = {
				200000033L, 200000039L, 200000051L, 200000069L,
				200000081L, 200000083L, 200000089L, 200000093L,
				4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
		};
		//// long    vectorNumeros[] = {
		//// 200000033L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		//// 200000039L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		//// 200000051L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		//// 200000069L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		//// 200000081L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		//// 200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		//// 200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		//// 200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
		//// };
		// Comprobacion y extraccion de los argumentos de entrada.
		if( args.length != 1 ) {
			System.err.println( "Uso: java programa <numHebras>" );
			System.exit( -1 );
		}
		try {
			numHebras = Integer.parseInt( args[ 0 ] );
		} catch( NumberFormatException ex ) {
			numHebras = -1;
			System.out.println( "ERROR: Argumentos numericos incorrectos." );
			System.exit( -1 );
		}
		//
		// Implementacion secuencial.
		//
		System.out.println( "" );
		System.out.println( "Implementacion secuencial." );
		t1 = System.nanoTime();
		for( int i = 0; i < vectorNumeros.length; i++ ) {
			if( esPrimo( vectorNumeros[ i ] ) ) {
				System.out.println( "  Encontrado primo: " + vectorNumeros[ i ] );
			}
		}
		t2 = System.nanoTime();
		ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Tiempo secuencial (seg.):                    " + ts );

		//
		// Implementacion paralela ciclica.
		//
		System.out.println( "" );
		System.out.println( "Implementacion paralela ciclica." );
		t1 = System.nanoTime();

		//
		// Gestion de hebras para la implementacion paralela ciclica
		//

		MiHebraCiclica hc[] = new MiHebraCiclica[numHebras];
		for(int i = 0; i < numHebras; i++) {
			hc[i] = new MiHebraCiclica(i, numHebras, vectorNumeros);
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
		tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Tiempo paralela ciclica (seg.):              " + tc );
		System.out.println( "Incremento paralela ciclica:                 " + (ts/tc) );
		//
		// Implementacion paralela por bloques.
		//
		// ....
		//
		// Implementacion paralela dinamica.
		//
		// ....

	}

	// -------------------------------------------------------------------------
	static boolean esPrimo( long num ) {
		boolean primo;
		if( num < 2 ) {
			primo = false;
		} else {
			primo = true;
			long i = 2;
			while( ( i < num )&&( primo ) ) {
				primo = ( num % i != 0 );
				i++;
			}
		}
		return( primo );
	}
}
