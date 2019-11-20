package LA06;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

// ============================================================================
class EjemploPalabraMasUsada1a {
// ============================================================================

	// -------------------------------------------------------------------------
	public static void main( String args[] ) {
		long                     t1, t2;
		double                   tt, tt1, tt2, tt3, tt4, tt5, tt6, tt7;
		int                      numHebras;
		String                   nombreFichero, palabraActual;
		ArrayList<String>        arrayLineas;
		HashMap<String,Integer>  hmCuentaPalabras;

		// Comprobacion y extraccion de los argumentos de entrada.
		if( args.length != 2 ) {
			System.err.println( "Uso: java programa <numHebras> <fichero>" );
			System.exit( -1 );
		}
		try {
			numHebras     = Integer.parseInt( args[ 0 ] );
			nombreFichero = args[ 1 ];
		} catch( NumberFormatException ex ) {
			numHebras = -1;
			nombreFichero = "";
			System.out.println( "ERROR: Argumentos numericos incorrectos." );
			System.exit( -1 );
		}

		// Lectura y carga de lineas en "arrayLineas".
		arrayLineas = readFile( nombreFichero );
		System.out.println( "Numero de lineas leidas: " + arrayLineas.size() );
		System.out.println();

		//
		// Implementacion secuencial sin temporizar.
		//
		hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
		for( int i = 0; i < arrayLineas.size(); i++ ) {
			// Procesa la linea "i".
			String[] palabras = arrayLineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				// Procesa cada palabra de la linea "i", si es distinta de blancos.
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					contabilizaPalabra( hmCuentaPalabras, palabraActual );
				}
			}
		}

		//
		// Implementacion secuencial.
		//
		t1 = System.nanoTime();
		hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
		for( int i = 0; i < arrayLineas.size(); i++ ) {
			// Procesa la linea "i".
			String[] palabras = arrayLineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				// Procesa cada palabra de la linea "i", si es distinta de blancos.
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					contabilizaPalabra( hmCuentaPalabras, palabraActual );
				}
			}
		}
		t2 = System.nanoTime();
		tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. secuencial: " );
		imprimePalabraMasUsadaYVeces( hmCuentaPalabras );
		System.out.println( " Tiempo(s): " + tt );
		System.out.println( "Num. elems. tabla hash: " + hmCuentaPalabras.size() );
		System.out.println();


		//
		// Implementacion paralela 1: Uso de synchronizedMap.
		//
		t1 = System.nanoTime();
		HashMap<String, Integer> cuentaPalabras1 = new HashMap<String, Integer>(1000, 0.75F);
		MiHebra_1[] hebras_1 = new MiHebra_1[numHebras];
		for (int i = 0; i < numHebras; i++) {
			hebras_1[i] = new MiHebra_1(i, numHebras, cuentaPalabras1, arrayLineas);
			hebras_1[i].start();
		}
		for (int i = 0; i < numHebras; i++) {
			try {
				hebras_1[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt1 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela 1: " );
		imprimePalabraMasUsadaYVeces( cuentaPalabras1 );
		System.out.println( " Tiempo(s): " + tt1  + " , Incremento " + tt/tt1);
		System.out.println( "Num. elems. tabla hash: " + cuentaPalabras1.size() );
		System.out.println();

		//
		// Implementacion paralela 2: Uso de Hashtable.
		//
		t1 = System.nanoTime();
		Hashtable<String, Integer> cuentaPalabras2 = new Hashtable<String, Integer>(1000, 0.75F);
		MiHebra_2[] hebras_2 = new MiHebra_2[numHebras];
		for (int i = 0; i < numHebras; i++) {
			hebras_2[i] = new MiHebra_2(i, numHebras, cuentaPalabras2, arrayLineas);
			hebras_2[i].start();
		}
		for (int i = 0; i < numHebras; i++) {
			try {
				hebras_2[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt2 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela 2: " );
		imprimePalabraMasUsadaYVeces( cuentaPalabras2 );
		System.out.println( " Tiempo(s): " + tt2  + " , Incremento " + tt/tt2);
		System.out.println( "Num. elems. tabla hash: " + cuentaPalabras2.size() );
		System.out.println();

		//
		// Implementacion paralela 3: Uso de ConcurrentHashMap
		//
		t1 = System.nanoTime();
		ConcurrentHashMap<String, Integer> cuentaPalabras3 = new ConcurrentHashMap<String, Integer>(1000, 0.75F);
		MiHebra_3[] hebras_3 = new MiHebra_3[numHebras];
		for (int i = 0; i < numHebras; i++) {
			hebras_3[i] = new MiHebra_3(i, numHebras, cuentaPalabras3, arrayLineas);
			hebras_3[i].start();
		}
		for (int i = 0; i < numHebras; i++) {
			try {
				hebras_3[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt3 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela 3: " );
		imprimePalabraMasUsadaYVeces( cuentaPalabras3 );
		System.out.println( " Tiempo(s): " + tt3  + " , Incremento " + tt/tt3);
		System.out.println( "Num. elems. tabla hash: " + cuentaPalabras3.size() );
		System.out.println();

		//
		// Implementacion paralela 4: Uso de ConcurrentHashMap
		//
		t1 = System.nanoTime();
		ConcurrentHashMap<String, Integer> cuentaPalabras4 = new ConcurrentHashMap<String, Integer>(1000, 0.75F);
		MiHebra_4 hebras_4[] = new MiHebra_4[numHebras];
		for (int i = 0; i < numHebras; i++) {
			hebras_4[i] = new MiHebra_4(i, numHebras, cuentaPalabras4, arrayLineas);
			hebras_4[i].start();
		}
		for (int i = 0; i < numHebras; i++) {
			try {
				hebras_4[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt4 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela 4: " );
		imprimePalabraMasUsadaYVeces( cuentaPalabras4 );
		System.out.println( " Tiempo(s): " + tt4  + " , Incremento " + tt/tt4);
		System.out.println( "Num. elems. tabla hash: " + cuentaPalabras4.size() );
		System.out.println();

		//
		// Implementacion paralela 5: Uso de ConcurrentHashMap
		//
		t1 = System.nanoTime();
		ConcurrentHashMap<String, AtomicInteger> cuentaPalabras5 = new ConcurrentHashMap<String, AtomicInteger>(1000, 0.75F);
		MiHebra_5 hebras_5[] = new MiHebra_5[numHebras];
		for (int i = 0; i < numHebras; i++) {
			hebras_5[i] = new MiHebra_5(i, numHebras, cuentaPalabras5, arrayLineas);
			hebras_5[i].start();
		}
		for (int i = 0; i < numHebras; i++) {
			try {
				hebras_5[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt5 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela 5: " );
		imprimePalabraMasUsadaYVecesAtomic( cuentaPalabras5 );
		System.out.println( " Tiempo(s): " + tt5  + " , Incremento " + tt/tt5);
		System.out.println( "Num. elems. tabla hash: " + cuentaPalabras5.size() );
		System.out.println();

		//
		// Implementacion paralela 6: Uso de ConcurrentHashMap
		//
		t1 = System.nanoTime();
		ConcurrentHashMap<String, AtomicInteger> cuentaPalabras6 = new ConcurrentHashMap<String, AtomicInteger>(1000, 0.75F, 256);
		MiHebra_6 hebras_6[] = new MiHebra_6[numHebras];
		for (int i = 0; i < numHebras; i++) {
			hebras_6[i] = new MiHebra_6(i, numHebras, cuentaPalabras6, arrayLineas);
			hebras_6[i].start();
		}
		for (int i = 0; i < numHebras; i++) {
			try {
				hebras_6[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt6 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela 6: " );
		imprimePalabraMasUsadaYVecesAtomic( cuentaPalabras6 );
		System.out.println( " Tiempo(s): " + tt6  + " , Incremento " + tt/tt6);
		System.out.println( "Num. elems. tabla hash: " + cuentaPalabras6.size() );
		System.out.println();

		//
		// Implementacion paralela 7: Uso de Streams
		//
		t1 = System.nanoTime();
		Map<String, Long> cuentaPalabras7 = arrayLineas.parallelStream()
			.filter(s -> s != null)
			.map(s -> s.split("\\W+"))
			.flatMap(Arrays::stream)
			.map(String::trim)
			.filter(s -> (s.length() > 0))
			.collect(groupingBy(s -> s, counting()));
		t2 = System.nanoTime();
		tt7 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela x: " );
		imprimePalabraMasUsadaYVecesLong( cuentaPalabras7 );
		System.out.println( " Tiempo(s): " + tt7  + " , Incremento " + tt/tt7);
		System.out.println( "Num. elems. tabla hash: " + cuentaPalabras7.size() );
		System.out.println();

		System.out.println( "Fin de programa." );
	}

	// -------------------------------------------------------------------------
	public static ArrayList<String> readFile( String fileName ) {
		BufferedReader    br;
		String            linea;
		ArrayList<String> data = new ArrayList<String>();

		try {
			br = new BufferedReader( new FileReader( fileName ) );
			while( ( linea = br.readLine() ) != null ) {
				//// System.out.println( "Leida linea: " + linea );
				data.add( linea );
			}
			br.close();
		} catch( FileNotFoundException ex ) {
			ex.printStackTrace();
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
		return data;
	}

	// -------------------------------------------------------------------------
	public static void contabilizaPalabra( HashMap<String,Integer> cuentaPalabras, String palabra ) {
		Integer numVeces = cuentaPalabras.get( palabra );
		if( numVeces != null ) {
			cuentaPalabras.put( palabra, numVeces+1 );
		} else {
			cuentaPalabras.put( palabra, 1 );
		}
	}

	// --------------------------------------------------------------------------
	static void imprimePalabraMasUsadaYVeces(
			Map<String,Integer> cuentaPalabras ) {
		ArrayList<Map.Entry> lista =
				new ArrayList<Map.Entry>( cuentaPalabras.entrySet() );

		String palabraMasUsada = "";
		int    numVecesPalabraMasUsada = 0;
		// Calcula la palabra mas usada.
		for( int i = 0; i < lista.size(); i++ ) {
			String palabra = ( String ) lista.get( i ).getKey();
			int numVeces = ( Integer ) lista.get( i ).getValue();
			if( i == 0 ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			} else if( numVecesPalabraMasUsada < numVeces ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			}
		}
		// Imprime resultado.
		System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
				"veces: " + numVecesPalabraMasUsada + " )" );
	}

	// --------------------------------------------------------------------------
	static void imprimePalabraMasUsadaYVecesAtomic(
			Map<String,AtomicInteger> cuentaPalabras ) {
		ArrayList<Map.Entry> lista =
				new ArrayList<Map.Entry>( cuentaPalabras.entrySet() );

		String palabraMasUsada = "";
		int    numVecesPalabraMasUsada = 0;
		// Calcula la palabra mas usada.
		for( int i = 0; i < lista.size(); i++ ) {
			String palabra = ( String ) lista.get( i ).getKey();
			int numVeces = (( AtomicInteger ) lista.get( i ).getValue()).get();
			if( i == 0 ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			} else if( numVecesPalabraMasUsada < numVeces ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			}
		}
		// Imprime resultado.
		System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
				"veces: " + numVecesPalabraMasUsada + " )" );
	}

	static void imprimePalabraMasUsadaYVecesLong(
			Map<String,Long> cuentaPalabras ) {
		ArrayList<Map.Entry> lista =
				new ArrayList<Map.Entry>( cuentaPalabras.entrySet() );

		String palabraMasUsada = "";
		long numVecesPalabraMasUsada = 0;
		// Calcula la palabra mas usada.
		for( int i = 0; i < lista.size(); i++ ) {
			String palabra = ( String ) lista.get( i ).getKey();
			long numVeces = ( Long ) lista.get( i ).getValue();
			if( i == 0 ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			} else if( numVecesPalabraMasUsada < numVeces ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			}
		}
		// Imprime resultado.
		System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
				"veces: " + numVecesPalabraMasUsada + " )" );
	}

	// --------------------------------------------------------------------------
	static void printCuentaPalabrasOrdenadas(
			HashMap<String,Integer> cuentaPalabras ) {
		int             i, numVeces;
		List<Map.Entry> list = new ArrayList<Map.Entry>( cuentaPalabras.entrySet() );

		// Ordena por valor.
		Collections.sort(
				list,
				new Comparator<Map.Entry>() {
					public int compare( Map.Entry e1, Map.Entry e2 ) {
						Integer i1 = ( Integer ) e1.getValue();
						Integer i2 = ( Integer ) e2.getValue();
						return i2.compareTo( i1 );
					}
				}
		);
		// Muestra contenido.
		i = 1;
		System.out.println( "Veces Palabra" );
		System.out.println( "-----------------" );
		for( Map.Entry e : list ) {
			numVeces = ( ( Integer ) e.getValue () ).intValue();
			System.out.println( i + " " + e.getKey() + " " + numVeces );
			i++;
		}
		System.out.println( "-----------------" );
	}
}

class MiHebra_1 extends Thread {
	private int miId, numHebras;
	private HashMap<String, Integer> hmCuentaPalabras;
	private ArrayList<String> arrayLineas;

	MiHebra_1(int miId, int numHebras, HashMap<String, Integer> hmCuentaPalabras, ArrayList<String> arrayLineas) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.hmCuentaPalabras = hmCuentaPalabras;
		this.arrayLineas = arrayLineas;
	}

	public void run() {
		String palabraActual;
		for(int i = miId; i < arrayLineas.size(); i += numHebras) {
			String[] palabras = arrayLineas.get(i).split( "\\W+" );
			for (String palabra : palabras) {
				palabraActual = palabra.trim();
				if (palabraActual.length() > 0) {
					contabilizaPalabra(hmCuentaPalabras, palabraActual);
				}
			}
		}
	}

	synchronized private static void contabilizaPalabra(HashMap<String, Integer> cuentaPalabras, String palabra) {
		cuentaPalabras.merge(palabra, 1, (oldVal, newVal) -> oldVal + newVal);
	}

}

class MiHebra_2 extends Thread {
	private int miId, numHebras;
	private Hashtable<String, Integer> hmCuentaPalabras;
	private ArrayList<String> arrayLineas;

	MiHebra_2(int miId, int numHebras, Hashtable<String, Integer> hmCuentaPalabras, ArrayList<String> arrayLineas) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.hmCuentaPalabras = hmCuentaPalabras;
		this.arrayLineas = arrayLineas;
	}

	public void run() {
		String palabraActual;
		for(int i = miId; i < arrayLineas.size(); i += numHebras) {
			String[] palabras = arrayLineas.get(i).split( "\\W+" );
			for (String palabra : palabras) {
				palabraActual = palabra.trim();
				if (palabraActual.length() > 0) {
					contabilizaPalabra(hmCuentaPalabras, palabraActual);
				}
			}
		}
	}

	private static void contabilizaPalabra(Hashtable<String, Integer> cuentaPalabras, String palabra) {
		cuentaPalabras.merge(palabra, 1, (oldVal, newVal) -> oldVal + newVal);
	}

}

class MiHebra_3 extends Thread {
	private int miId, numHebras;
	private ConcurrentHashMap<String, Integer> hmCuentaPalabras;
	private ArrayList<String> arrayLineas;

	MiHebra_3(int miId, int numHebras, ConcurrentHashMap<String, Integer> hmCuentaPalabras, ArrayList<String> arrayLineas) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.hmCuentaPalabras = hmCuentaPalabras;
		this.arrayLineas = arrayLineas;
	}

	public void run() {
		String palabraActual;
		for(int i = miId; i < arrayLineas.size(); i += numHebras) {
			String[] palabras = arrayLineas.get(i).split( "\\W+" );
			for (String palabra : palabras) {
				palabraActual = palabra.trim();
				if (palabraActual.length() > 0) {
					contabilizaPalabra(hmCuentaPalabras, palabraActual);
				}
			}
		}
	}

	synchronized private static void contabilizaPalabra(ConcurrentHashMap<String, Integer> cuentaPalabras, String palabra) {
		cuentaPalabras.merge(palabra, 1, (oldVal, newVal) -> oldVal + newVal);
	}

}

class MiHebra_4 extends Thread{
	private int miId, numHebras;
	private ConcurrentHashMap<String, Integer> hmCuentaPalabras;
	private ArrayList<String> arrayLineas;

	MiHebra_4(int miId, int numHebras, ConcurrentHashMap<String, Integer> hmCuentaPalabras, ArrayList<String> arrayLineas) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.hmCuentaPalabras = hmCuentaPalabras;
		this.arrayLineas = arrayLineas;
	}

	public void run() {
		String palabraActual;
		for(int i = miId; i < arrayLineas.size(); i += numHebras) {
			String[] palabras = arrayLineas.get(i).split( "\\W+" );
			for (String palabra : palabras) {
				palabraActual = palabra.trim();
				if (palabraActual.length() > 0) {
					contabilizaPalabra(hmCuentaPalabras, palabraActual);
				}
			}
		}
	}

	private static void contabilizaPalabra(ConcurrentHashMap<String, Integer> cuentaPalabras, String palabra) {
		Integer numVeces = cuentaPalabras.putIfAbsent( palabra, 1 );
		boolean modif;
		if (numVeces != null) {
			do {
				numVeces = cuentaPalabras.get(palabra);
				modif = cuentaPalabras.replace( palabra, numVeces, numVeces+1 );
			} while(!modif);
		}
	}
}

class MiHebra_5 extends Thread{
	private int miId, numHebras;
	private ConcurrentHashMap<String, AtomicInteger> hmCuentaPalabras;
	private ArrayList<String> arrayLineas;

	MiHebra_5(int miId, int numHebras, ConcurrentHashMap<String, AtomicInteger> hmCuentaPalabras, ArrayList<String> arrayLineas) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.hmCuentaPalabras = hmCuentaPalabras;
		this.arrayLineas = arrayLineas;
	}

	public void run() {
		String palabraActual;
		for(int i = miId; i < arrayLineas.size(); i += numHebras) {
			String[] palabras = arrayLineas.get(i).split( "\\W+" );
			for (String palabra : palabras) {
				palabraActual = palabra.trim();
				if (palabraActual.length() > 0) {
					contabilizaPalabra(hmCuentaPalabras, palabraActual);
				}
			}
		}
	}

	private static void contabilizaPalabra(ConcurrentHashMap<String, AtomicInteger> cuentaPalabras, String palabra) {
		AtomicInteger numVeces = cuentaPalabras.putIfAbsent( palabra, new AtomicInteger(1) );
		if (numVeces != null) {
			numVeces.getAndIncrement();
		}
	}
}

class MiHebra_6 extends Thread{
	private int miId, numHebras;
	private ConcurrentHashMap<String, AtomicInteger> hmCuentaPalabras;
	private ArrayList<String> arrayLineas;

	MiHebra_6(int miId, int numHebras, ConcurrentHashMap<String, AtomicInteger> hmCuentaPalabras, ArrayList<String> arrayLineas) {
		this.miId = miId;
		this.numHebras = numHebras;
		this.hmCuentaPalabras = hmCuentaPalabras;
		this.arrayLineas = arrayLineas;
	}

	public void run() {
		String palabraActual;
		for(int i = miId; i < arrayLineas.size(); i += numHebras) {
			String[] palabras = arrayLineas.get(i).split( "\\W+" );
			for (String palabra : palabras) {
				palabraActual = palabra.trim();
				if (palabraActual.length() > 0) {
					contabilizaPalabra(hmCuentaPalabras, palabraActual);
				}
			}
		}
	}

	private static void contabilizaPalabra(ConcurrentHashMap<String, AtomicInteger> cuentaPalabras, String palabra) {
		AtomicInteger numVeces = cuentaPalabras.putIfAbsent( palabra, new AtomicInteger(1) );
		if (numVeces != null) {
			numVeces.getAndIncrement();
		}
	}

}

/*
		hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
		for( int i = 0; i < arrayLineas.size(); i++ ) {
			// Procesa la linea "i".
			String[] palabras = arrayLineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				// Procesa cada palabra de la linea "i", si es distinta de blancos.
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					contabilizaPalabra( hmCuentaPalabras, palabraActual );
				}
			}
		}
 */