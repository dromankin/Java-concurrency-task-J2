package ru.nsu.romankin.hoi;

import java.util.*;

public class Main {
    private volatile long totalSortSteps = 0;

    private final MyList<String> myList;
    private List<String> list;
    private int delay;
    private int threadNum;
    private boolean useMyList;
    private List<SortThread> threads;
    public Main(int delay, int threadNum, boolean useMyList) {
        this.delay = delay;
        this.threadNum = threadNum;
        this.useMyList = useMyList;
        if (!useMyList) {
            list = Collections.synchronizedList(new ArrayList<>());
            myList = null;
        } else {
            myList = new MyList<>();
            list = null;
        }

        threads = new ArrayList<>();
    }

    private void start() {
        for (int i = 0; i < threadNum; i++) {
            SortThread thread = new SortThread(i + 1);
            threads.add(thread);
            thread.start();
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                printList();
            } else {
                addAndSplitLargeString(input);
            }
        }
    }

    private void addAndSplitLargeString(String input) {
        if (input.length() <= 80) {
            addToList(input);
        } else {
            for (int i = 0; i < input.length(); i += 80) {
                int end = Math.min(i + 80, input.length());
                String part = input.substring(i, end);
                addToList(part);
            }
        }
    }

    private void addToList(String string) {
        if (useMyList) {

            myList.add(string);

        } else {
            
            list.add(0, string);
            
        }
    }
    private class SortThread extends Thread {
        private volatile boolean running = true;
        private final int id;
        public SortThread(int id) {
            this.id = id; 
        }

        @Override
        public void run() {
            System.out.println("Thread " + id +" has been launched" );
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    if (useMyList) {
                        sortStepCustom();
                    } else {
                        sortStepLibrary();
                    }

                    if (delay > 0) {
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error in thread " + id + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        private void sortStepCustom() throws InterruptedException {
            synchronized (myList) {
                if (myList.size() < 2) {
                    return;
                }

                boolean swapped = false;

                for (int i = 0; i < myList.size() - 1 && running; i++) {
                    totalSortSteps++;
                    String current = myList.get(i);
                    String next = myList.get(i + 1);

                    if (current.compareTo(next) > 0) {
                        myList.swap(i);
                        swapped = true;
                        if (delay > 0) {
                            Thread.sleep(delay);
                        }
                    }
                }

                if (!swapped && running) {
                    Thread.sleep(delay);
                }
            }
        }

        private void sortStepLibrary() throws InterruptedException {
            synchronized (list) {
                if (list.size() < 2) {
                    return;
                }

                boolean swapped = false;

                for (int i = 0; i < list.size() - 1 && running; i++) {
                    totalSortSteps++;
                    String current = list.get(i);
                    String next = list.get(i + 1);

                    if (current.compareTo(next) > 0) {
                        list.set(i, next);
                        list.set(i + 1, current);
                        swapped = true;
                        if (delay > 0) {
                            Thread.sleep(delay);
                        }
                    }
                }

                if (!swapped && running) {
                    Thread.sleep(delay);
                }
            }
        }
    }

    private void printList() {
        if (useMyList) {
            for (String data : myList) {
                System.out.println(data);
            }
        } else {
            for (String data : list) {
                System.out.println(data);
            }
        }
        System.out.println("Total sort steps: " + totalSortSteps);
    }

    
    public static void main(String[] args) {
        int threadNum = Integer.parseInt(args[0]);
        int delayMs = Integer.parseInt(args[1]);
        boolean useMylist = Boolean.parseBoolean(args[2]);

        Main main = new Main(delayMs, threadNum, useMylist);

        main.start();
    }
}