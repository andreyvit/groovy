// Copyright (c) 2001 The Wilson Partnership.
// All Rights Reserved.
// @(#)MinMLSocketServer.java, 0.1, 6th July 2001
// Author: John Wilson - tug@wilson.co.uk

package uk.co.wilson.net;

/*
Copyright (c) 2001 John Wilson (tug@wilson.co.uk).
All rights reserved.
Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

Redistributions of source code must retain the above
copyright notice, this list of conditions and the
following disclaimer.

Redistributions in binary form must reproduce the
above copyright notice, this list of conditions and
the following disclaimer in the documentation and/or
other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY JOHN WILSON ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JOHN WILSON
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE
*/

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import java.io.InterruptedIOException;
import java.io.IOException;

public abstract class MinMLSocketServer{
  public MinMLSocketServer(final ServerSocket serverSocket,
                           final int minWorkers,
                           final int maxWorkers,
                           final int workerIdleLife)
  {
    this.serverSocket = serverSocket;
    this.minWorkers = Math.max(minWorkers, 1);
    this.maxWorkers = Math.max(this.minWorkers, maxWorkers);
    this.workerIdleLife = workerIdleLife;
  }

  public void start() {
    getNewWorker().run();
  }

  public synchronized void shutDown() throws IOException {
    this.serverSocket.close();
  }

  public int getPortNumber() {
    return this.serverSocket.getLocalPort();
  }

  protected abstract Worker makeNewWorker();

  private void setsocketTimeout(final int timeout) {
    try {
      this.serverSocket.setSoTimeout(timeout);
    }
    catch (final SocketException e) {
    }
  }

  private Worker getNewWorker() {
if (debug) System.out.println("Starting new thread: liveWorkerCount = " + this.liveWorkerCount + " workingWorkerCount = " + this.workingWorkerCount);
    if (this.liveWorkerCount++ == this.minWorkers)
      setsocketTimeout(this.workerIdleLife);

    return makeNewWorker();
  }

  private synchronized void startWork() {
    if (++this.workingWorkerCount == this.liveWorkerCount && this.liveWorkerCount < this.maxWorkers)
      new Thread(getNewWorker()).start();
if (debug) System.out.println("Thread starting work: liveWorkerCount = " + this.liveWorkerCount + " workingWorkerCount = " + this.workingWorkerCount);
  }

  private synchronized void endWork() {
    this.workingWorkerCount--;
if (debug) System.out.println("Thread ending work: liveWorkerCount = " + this.liveWorkerCount + " workingWorkerCount = " + this.workingWorkerCount);
  }

  private synchronized boolean workerMustDie() {
if (debug) System.out.println("Thread timing out socket read: liveWorkerCount = " + this.liveWorkerCount + " workingWorkerCount = " + this.workingWorkerCount);
    if (this.liveWorkerCount > this.minWorkers && this.liveWorkerCount != this.workingWorkerCount + 1) {
if (debug) System.out.println("Thread commits suicide");
      workerDies();

      return true;
    }

    return false;
  }

  private synchronized void workerDies() {
    if (--this.liveWorkerCount == this.minWorkers)
      setsocketTimeout(0);
if (debug) System.out.println("Thread dying: liveWorkerCount = " + this.liveWorkerCount + " workingWorkerCount = " + this.workingWorkerCount);
  }

  protected abstract class Worker implements Runnable {
    public final void run() {
      try {
        while (true) {
        final Socket socket;

          try {
            socket = MinMLSocketServer.this.serverSocket.accept();

            try {
              try {
                MinMLSocketServer.this.startWork();

                Thread.yield(); // let a blocked worker thread do an accept()

                process(socket);
              }
              catch (final Exception e) {
                processingException(e);
              }
            }
            finally {
              try {
                socket.close();
              }
              catch (final IOException e) {
if (debug) {
  System.out.println("Exception thrown when closing socket: " + e.toString());
  e.printStackTrace();
}
              }
              finally {
                MinMLSocketServer.this.endWork();
              }
            }
          }
          catch (final InterruptedIOException e) {
            if (MinMLSocketServer.this.workerMustDie()) return;
          }
        }
      }
      catch (final Exception e) {
        operatingException(e);
if (debug) {
  System.out.println("Thread dying due to Exception: " + e.toString());
  e.printStackTrace();
}

        MinMLSocketServer.this.workerDies();
      }
    }

    protected abstract void process(Socket socket) throws Exception;

    protected void processingException(final Exception e) {
    }

    protected void operatingException(final Exception e) {
    }
  }

  private final ServerSocket serverSocket;
  protected final int minWorkers;
  protected final int maxWorkers;
  protected final int workerIdleLife;
  private int liveWorkerCount = 0;
  private int workingWorkerCount = 0;

  private static final boolean debug = false;
}
