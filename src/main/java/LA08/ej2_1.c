#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"

int main( int argc, char * argv[] ) {
	int numProcs, miId, n, i;
	MPI_Status  s;
	MPI_Init(&argc, &argv);
	//…
	MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
	MPI_Comm_rank(MPI_COMM_WORLD, &miId);
	//…

	 if( miId == 0 ) {
		printf( "Dame numero:\n" );
		scanf( "%d", &n );
	}
	printf( "Proceso %d numero: %d\n", miId, n );

	MPI_Finalize();

	return 0;
}