package com.jssdvv.ara.core.domain.repository

import java.io.File

interface DirectoriesManager {

    /**
     * Directory path: ```com.jssdvv.ara.files/machine```
     */
    fun getRootMachineDir(): File

    /**
     * Base directory for each machine's files.This directory
     * is mainly used for store the other directories of the machine.
     *
     * Directory path: ```com.jssdvv.ara.files.machine/[machine_id]```
     */
    fun getMachineDir(machineId: Int): File

    /**
     * Exclusive directory used for storing the QR codes for recognition of each machine.
     * These markers are shared between all the activities of the machine.
     *
     * Directory path: ```com.jssdvv.ara.files.machine/[machine_id]/marker```
     */
    fun getMarkersDir(machineId: Int): File

    /**
     * Exclusive Directory for storing images and videos of each machine.
     * These files are shared for all features of the machine, including the procedures.
     *
     * Directory path: ```com.jssdvv.ara.files.machine/[machine_id]/activity```
     */
    fun getMediaFilesDir(machineId: Int): File

    /**
     * Exclusive directory for storing documents of each machine.
     * These files are shared for all features of the machine, including the procedures.
     *
     * Directory path: ```com.jssdvv.ara.files.machine/[machine_id]/document```
     */
    fun getDocumentsDir(machineId: Int): File

    /**
     * Exclusive directory for storing models of each machine. This files are shared
     * between the calibration and activities procedures of the machine:
     *
     * Directory path: ```com.jssdvv.ara.files/machine/[machine_id]/model```
     */
    fun getModelsDir(machineId: Int): File

    /**
     * Exclusive directory for storing vector files of the app.
     * These files are shared for machines feature.
     *
     * Directory path: ```com.jssdvv.ara.files/machine/vector```
     */
    fun getVectorDir(machineId: Int) : File

    /**
     * Exclusive directory for storing tool images.
     *
     * Directory path: ```com.jssdvv.ara.files/tools/media```
     */
    fun getToolsMediaDir(): File
}