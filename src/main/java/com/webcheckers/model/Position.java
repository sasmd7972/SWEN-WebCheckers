package com.webcheckers.model;

public class Position {

    private int row;  // { 0 to 7 }
    private int cell;  // { 0 to 7 }

    /**
     * Constructor for position
     * @param row the row number
     * @param cell the cell number
     */
    public Position( int row, int cell ) {
        this.row = row;
        this.cell = cell;
    }

    /**
     * The row index of this position
     * @return an int from 0 to 7
     */
    public int getRow() {
        return this.row;
    }


    /**
     * The cell (column) index of this position
     * @return an int from 0 to 7
     */
    public int getCell() {
        return this.cell;
    }

    /**
     * Compares another position with this one
     * @param other position to be compared with
     * @return
     *      0: if both positions are the same
     *      1: if the other position is larger
     *      -1: if this position is larger
     */
    public int compare( Position other ) {

        if ( this.equals( other ) ) {
            return 0;
        } else {
            if( this.row < other.getRow() ) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    /**
     * Returns the difference between the other row and this row
     * @param other position to be compared with
     * @return the distance in between
     *      +: if other is bigger
     *      -: if other is smaller
     */
    public int difference( Position other ) {
        return ( this.getRow() - other.getRow() );
    }

    /**
     * Returns a new Position object that is in between this position and other position
     * @param other position to be compared with
     * @return a new Position object
     */
    public Position between( Position other ) {
        int difRow = ( other.getRow() - this.row ) / 2;
        int difCell = ( other.getCell() - this.cell ) / 2;


        return new Position( this.row + difRow, this.cell + difCell );
    }

    /**
     * Checks if this position is in bounds
     * @return
     */
    public boolean inBounds() {
        return this.row <= 7 && this.row >= 0 && this.cell <= 7 && this.cell >= 0;
    }

    /**
     * Compares another object with this one
     * @param pos object to be compared with
     * @return
     *      true: if the object is the same as this or has the same row and cell
     *      false: if the object is not the same type or has a different row or cell
     */
    public boolean equals( Object pos ) {
        if(pos == this) return true;
        if(!(pos instanceof Position)) return false;

        final Position that = (Position) pos;

        return this.row == that.getRow() && this.cell == that.getCell();
    }
}
