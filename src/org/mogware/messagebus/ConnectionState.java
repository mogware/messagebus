package org.mogware.messagebus;

/**
* Represents the state of the underlying connection at various critical points.
*/

public enum ConnectionState
{
    /**
    * The connection is closed and no operations can be performed until the
    * connection is reestablished.
    */
    Closed,

    /**
    * The connection is opening and being initialized.
    */
    Opening,

    /**
    * The connection is open and ready for work.
    */
    Open,

    /**
    * The connection is shutting down and performing any cleanup necessary.
    */
    Closing,

    /**
    * The endpoint was previously available and attempts to re-open the
    * connection are being made.
    */
    Disconnected,

    /**
    * Indicates that the current security credentials are incorrect.
    */
    Unauthenticated,

    /**
    * Indicates that the current security context does not contain the
    * necessary privileges.
    */
    Unauthorized
}
