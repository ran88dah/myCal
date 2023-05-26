package ce.yildiz.edu.tr.calendar.views;

public class typeItem {


        private String mTypeName;
        private int mTypeImage;

        public typeItem(String TypeName, int TypeImage) {
            mTypeName= TypeName;
            mTypeImage = TypeImage;
        }

        public String getTypeName() {
            return mTypeName;
        }

        public int getTypeImage() {
            return mTypeImage;
        }

}
