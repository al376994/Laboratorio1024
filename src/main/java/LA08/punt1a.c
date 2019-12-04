#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"

// ============================================================================
int main( int argc, char * argv[] ) {
	// Declaration of variables.
	MPI_Status  s;
	int         numProcs, miId, numArgs = 3, vecArgs[ 4 ];
	int         numMensajes, minTam, maxTam, incTam, tam, i, j;
	char        * ptrWorkspace;
	double      t1, t2, te = -1.0, tm, mbs;
	char        miNombre[ MPI_MAX_PROCESSOR_NAME ];
	int         lonMiNombre;
	
	// Inicializa MPI.
	MPI_Init( & argc, & argv );
	MPI_Comm_size( MPI_COMM_WORLD, & numProcs );
	MPI_Comm_rank( MPI_COMM_WORLD, & miId );
	
	// En primer lugar se comprueba si el numero de procesos es valido
	if( numProcs < 2 ) {
		if ( miId == 0 ) {
			fprintf( stderr, "\n" );
			fprintf( stderr, "Error: Al menos se deben iniciar dos procesos\n" );
			fprintf( stderr, "\n" );
		}
		MPI_Finalize();
		return( -1 );
	}
	
	// Imprime el nombre de los procesadores.
	MPI_Get_processor_name( miNombre, & lonMiNombre );
	printf( "Proceso %d  Se ejecuta en: %s\n", miId, miNombre );

	// El proceso 0 difunde el numero de argumentos de entrada al resto.
	// Todos los procesos deben tener dicho numero en la variable numArgs.
	if( miId == 0 ) {
		numArgs = argc;
	}
	// Se completa la operacion de difusion de numArgs.		####	A	 ####
	if( miId == 0 ) {
		for(i = 1; i < numProcs; i++){
			MPI_Send( &numArgs, 1, MPI_INT, i, 88, MPI_COMM_WORLD );
		}
	} else {
		MPI_Recv( &numArgs, 1, MPI_INT, 0, 88, MPI_COMM_WORLD, &s );
	}

	// Todos los procesos comprueban el numero de argumentos de entrada.
	if( ( numArgs != 3 )&&( numArgs != 5 ) ) {
		if ( miId == 0 ) {
			fprintf( stderr, "\n" );
			fprintf( stderr, "Uso A: a.out numMensajes tam\n" );
			fprintf( stderr, "Uso B: a.out numMensajes minTam maxTam incTam\n" );
			fprintf( stderr, "\n" );
		}
		MPI_Finalize();
		return( -1 );
	}
	
	// El proceso 0 interpreta la linea de comando
	if( miId == 0 ) {
		numMensajes = atoi( argv[ 1 ] );
		minTam  = atoi( argv[ 2 ] );
		if( numArgs == 5 ) {
			maxTam = atoi( argv[ 3 ] );
			incTam = atoi( argv[ 4 ] );
		} else {
			maxTam = minTam;
			incTam = 1;
		}
	}
	
	// El proceso 0 difunde los valores de la linea de argumentos al resto.
	// Todos los procesos deben tener dichos valores en el vector vecArgs.
	if( miId == 0 ) {
		vecArgs[ 0 ] = numMensajes;
		vecArgs[ 1 ] = minTam;
		vecArgs[ 2 ] = maxTam;
		vecArgs[ 3 ] = incTam;
	}
	// Se completa la operacion de difusion del vector vecArgs.
	if( miId == 0 ) {
    		for(i = 1; i < numProcs; i++){
    			MPI_Send( vecArgs, 4, MPI_INT, i, 88, MPI_COMM_WORLD );
    		}
    	} else {
    		MPI_Recv( vecArgs, 4, MPI_INT, 0, 88, MPI_COMM_WORLD, &s );
	}
	
	// Todos los procesos extraen la informacion almacena en el vector vecArgs.
	numMensajes = vecArgs[ 0 ];
	minTam      = vecArgs[ 1 ];
	maxTam      = vecArgs[ 2 ];
	incTam      = vecArgs[ 3 ]; if( incTam == 0 ) incTam++;
	
	// Imprime los parametros de trabajo.
	if( miId == 0 ) {
		printf( "  Number of processors: %5d\n", numProcs );
		printf( "  Number of messages:   %5d\n", numMensajes );
		printf( "  Lower size:           %5d\n", minTam );
		printf( "  Upper size:           %5d\n", maxTam );
		printf( "  Increment:            %5d\n", incTam );
	}
	
	// Crea el vector con espacio maximo.
	if( maxTam != 0 ) {
		ptrWorkspace = ( char * ) malloc( maxTam );
		if( ptrWorkspace == NULL ) {
			if ( miId == 0 ) {
			fprintf( stderr, "\n" );
			fprintf( stderr, "Error in Malloc: Returned NULL.\n\n");
			fprintf( stderr, "\n" );
			}
			MPI_Finalize();
			return( -1 );
		}
	} else {
		ptrWorkspace = NULL;
	}
	
	// Imprime cabecera de la tabla.
	if ( miId == 0 ) {
		printf( " Comenzando bucle para envio de informacion\n\n");
		printf( "  Size(bytes)   Total time(sec.)" );
		printf( "   Time per msg(microsec.)   Bandwith(MB/s)\n");
		printf( "--------------------------------" );
		printf( "--------------------------------------\n");
	}
	
	for( tam = minTam; tam <= maxTam; tam += incTam ) {
	
		// Sincronizacion de todos los procesos
		MPI_Barrier( MPI_COMM_WORLD );
	
		// Bucle de envio de "numMensajes" de dimension "tam" y toma de tiempos.
		// ... (C)
	
		// Calculo de prestaciones (te, tm, mbs).
		// ... (D)
	
		// Escritura de resultados.
		if( miId == 0 ){
			printf("   %8d", tam );
			if( te >= 0.0) {
			printf("   %15.6f", te );
			printf("   %15.9f", tm );
			printf("   %16.2f", mbs );
			printf("\n");
			} else {
			printf(": No se han realizado los calculos.\n" );
			}
		}
	
	}
	
	// Liberacion del espacio.
	if( maxTam != 0 ) {
		free( ptrWorkspace );
	}
	
	// Cierra MPI.
	MPI_Finalize();
	
	if ( miId == 0 ) {
		printf( " End of program\n" );
	}
	return 0;
}
