# include <stdio.h>
# include "mpi.h"

int main ( int argc , char * argv [] ) {

	int numProcs , miId, dato, suma;

	MPI_Init ( & argc , & argv ) ;

	MPI_Comm_size ( MPI_COMM_WORLD , & numProcs ) ;
	MPI_Comm_rank ( MPI_COMM_WORLD , & miId ) ;

	dato = numProcs - miId + 1;

	printf ( "Soy el proceso %d de %d  con el dato %d\n" ,miId , numProcs, dato );

	
	int zero = 0;
	MPI_Reduce((miId%2==0)?&dato:&zero, &suma, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);
	if(miId==0){
		printf("La suma es %d \n", suma);
	}

	MPI_Finalize () ;	
	printf ( "Fin de programa \n" ) ;
	return 0;
}