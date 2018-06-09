package com.example.administrator.xfmovie.entity;

import java.util.List;

public class TopService {

    /**
     * error_code : 200
     * reason : 请求成功！
     * result : {"total":337,"comicName":"斗破苍穹","chapterList":[{"name":"番外篇-熏儿不高兴","id":163463},{"name":"番外篇-白兔","id":163464},{"name":"番外","id":163465},{"name":"番外篇","id":163466},{"name":"第01话","id":163467},{"name":"第02话","id":163468},{"name":"第03话","id":163469},{"name":"第04话","id":163470},{"name":"第05话","id":163471},{"name":"第06话","id":163472},{"name":"第07话","id":163473},{"name":"第08话","id":163474},{"name":"第09话","id":163475},{"name":"第10话","id":163476},{"name":"第11话","id":163477},{"name":"第12话","id":163478},{"name":"第13话","id":163479},{"name":"第14话","id":163480},{"name":"第15话","id":163481},{"name":"第16话","id":163482}],"limit":20}
     */

    private int error_code;
    private String reason;
    private ResultBean result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * total : 337
         * comicName : 斗破苍穹
         * chapterList : [{"name":"番外篇-熏儿不高兴","id":163463},{"name":"番外篇-白兔","id":163464},{"name":"番外","id":163465},{"name":"番外篇","id":163466},{"name":"第01话","id":163467},{"name":"第02话","id":163468},{"name":"第03话","id":163469},{"name":"第04话","id":163470},{"name":"第05话","id":163471},{"name":"第06话","id":163472},{"name":"第07话","id":163473},{"name":"第08话","id":163474},{"name":"第09话","id":163475},{"name":"第10话","id":163476},{"name":"第11话","id":163477},{"name":"第12话","id":163478},{"name":"第13话","id":163479},{"name":"第14话","id":163480},{"name":"第15话","id":163481},{"name":"第16话","id":163482}]
         * limit : 20
         */

        private int total;
        private String comicName;
        private int limit;
        private List<ChapterListBean> chapterList;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getComicName() {
            return comicName;
        }

        public void setComicName(String comicName) {
            this.comicName = comicName;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public List<ChapterListBean> getChapterList() {
            return chapterList;
        }

        public void setChapterList(List<ChapterListBean> chapterList) {
            this.chapterList = chapterList;
        }

        public static class ChapterListBean {
            /**
             * name : 番外篇-熏儿不高兴
             * id : 163463
             */

            private String name;
            private int id;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}
