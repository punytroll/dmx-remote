import java.util.EventListener;

interface MetricListener extends EventListener
{
	public final int SIZE_CHANGED = 1;
	public final int MATRIX_CELL_SIZE_CHANGED = 2;
	public final int CELL_SIZE_CHANGED = 4;
	public final int ALL_CHANGED = 7;
	
	public void metricChanged(int WhatChanged);
}
