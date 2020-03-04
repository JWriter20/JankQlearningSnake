//  A Queue provided for the Spampede assignment
//  It should be better commented!
//

    class Queue implements QueueInterface
    {

        /*
         * an inner class, QCell, for the cells containing the Queue's data
         */
        class QCell
        {
            private Object data;
            private QCell next;

            private QCell(Object data)
            {
                this.data = data;
                this.next = next;
            }
        }

        // the two data members of each Queue object
        private QCell front;  // the front of the queue (for dequeueing)
        private QCell back;   // the back of the queue (for enqueueing)

        /*
         * a constructor of an empty Queue
         */
        public Queue()
        {
            front = null;
            back = null;
        }

        /*
         * isEmpty returns true if the Queue is empty; false otherwise
         */
        public boolean isEmpty()
        {
            return (front == null && back == null);
        }

        /*
         * enqueue adds an element (a QCell containing data) onto the back of this Queue
         */
        public void enqueue(Object data)
        {
            QCell new_back = new QCell(data);
            if (this.isEmpty()) {
                front = back = new_back;
            } else {
                back.next = new_back;
                back = new_back;
            }
            return;
        }

        /*
         * dequeue removes an element from the front of this Queue and returns its data
         */
        public Object dequeue()
        {
            if (isEmpty()) {
                System.out.println("You can't dequeue from an empty queue.");
                return null;
            }
            Object o = front.data;
            if (front == back) {
                front = back = null;
            } else {
                front = front.next;
            }
            return o;
        }

        /*
         * peek returns the data at the front of this Queue
         */
        public Object peek()
        {
            if (isEmpty())
            {
                System.out.println("You can't peek an empty Queue!");
                return null;
            }
            Object data = front.data;
            return data;
        }

        /*
         * peekNext returns the data in the second-from-front of this Queue
         */
        public Object peekNext()
        {
            if (isEmpty() || front.next == null)
            {
                System.out.println("You can't peekNext in a Queue with < 2 elements!");
                return null;
            }
            Object data = front.next.data;
            return data;
        }

        /*
         * toString returns the String representation of this Queue
         */
        public String toString()
        {
            String result = "<FRONT> ";
            QCell current = this.front;
            while (current != null)
            {
                result += "" + current.data + " ";
                current = current.next;
            }
            result += "<BACK>";
            return result;
        }

        /*
         * main tests our Queue class
         */
        public static void main(String[] args)
        {
            Queue ball = new Queue();
            System.out.println("ball is " + ball);
            ball.enqueue("Will");
            System.out.println("ball is " + ball);
            ball.enqueue("this");
            System.out.println("ball is " + ball);
            ball.enqueue("work?");
            System.out.println("ball is " + ball);
            ball.dequeue();
            System.out.println("ball is " + ball);
            ball.dequeue();
            System.out.println("ball is " + ball);
            ball.dequeue();
            System.out.println("ball is " + ball);
            ball.dequeue();
            System.out.println("ball is " + ball);
            ball.enqueue("Will");
            System.out.println("ball is " + ball);
            ball.enqueue("this");
            System.out.println("ball is " + ball);
            ball.enqueue("work?");
            System.out.println("ball is " + ball);
        }
    }



