# include <stdio.h>
# include "mpi.h"

int main ( int argc , char * argv [] ) {

	int numProcs , miId, dato, datoaux, i;
	MPI_Status s ;

	MPI_Init ( & argc , & argv ) ;

	MPI_Comm_size ( MPI_COMM_WORLD , & numProcs ) ;
	MPI_Comm_rank ( MPI_COMM_WORLD , & miId ) ;

	dato = numProcs - miId + 1;

	printf ( " Soy el proceso %d de %d  con el dato %d\n " ,miId , numProcs, dato );

	if(miId==0){
		int suma = dato;
		for(i=1; i<numProcs; i++){
			MPI_Recv(&datoaux,1,MPI_INT,MPI_ANY_SOURCE,88,MPI_COMM_WORLD,&s);
			suma += datoaux;
		}
		printf("La suma es de %d", suma);
	}
	else{
		MPI_Send(&dato, 1, MPI_INT,0,88, MPI_COMM_WORLD);
	}

	MPI_Finalize () ;	
	printf ( " Fin de programa \n " ) ;
	return 0;
}