    package businessLogic;

    import java.util.List;
    import java.util.NoSuchElementException;

    public class ExtendedIteratorFacade implements ExtendedIterator<Object> {
        private List<Object> list;
        private int position;

        public ExtendedIteratorFacade(List<Object> list) {
            this.list = list;
            this.position = -1;
        }

        @Override
        public Object previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            position--;
            return list.get(position);
        }

        @Override
        public boolean hasPrevious() {
            return position > 0;
        }

        @Override
        public void goFirst() {
            position = -1;
        }

        @Override
        public void goLast() {
            position = list.size();
        }

        @Override
        public boolean hasNext() {
            return position < list.size() - 1;
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            position++;
            return list.get(position);
        }
    }
