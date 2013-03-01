/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.denovogui.execution;

/**
 * <b>JobStatus</b>
 * <p>
 * Possible values for the job status are:
 * <p/>
 * WAITING - job is waiting for processing thread.
 * <p/>
 * RUNNING - job is running.
 * <p/>
 * FINISHED - job is finished.
 * <p/>
 * ERROR - job gave an error.
 * <p/>
 * CANCELED - job was canceled.
 * </p>
 *
 * @author T.Muth
 */
public enum JobStatus {

    WAITING, RUNNING, FINISHED, ERROR, CANCELED
    
}
