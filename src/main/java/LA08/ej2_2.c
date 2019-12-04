#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"

int main( int argc, char * argv[] ) {
	int numProcs, miId, n, i;
	MPI_Status s;
	MPI_Init(&argc, &argv);
	//…
	MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
	MPI_Comm_rank(MPI_COMM_WORLD, &miId);
	//…

	 if( miId == 0 ) {
		printf( "Dame numero:\n" );
		scanf( "%d", &n );
		for(i = 1; i < numProcs; i++){
			MPI_Send( &n, 1, MPI_INT, i, 88, MPI_COMM_WORLD );
		}
	} else {
		MPI_Recv( &n, 1, MPI_INT, 0, 88, MPI_COMM_WORLD, &s );
	}
	printf( "Proceso %d numero: %d\n", miId, n );

	MPI_Finalize();
	return 0;
}