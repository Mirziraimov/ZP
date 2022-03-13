package com.example.Modeles;

public class TblDatas {
    private Integer m_PP;
    private String m_FIO;
    private String m_ACC;
    private String m_SUMM;

    private int m_id;
    private String m_title;
    private String m_format;
    private String m_sighnature;
    private String m_key;

    public TblDatas(){};
    public TblDatas(Integer m_pp, String m_fio, String m_acc, String m_summ) {
        this.m_PP = m_pp;
        this.m_FIO = m_fio;
        this.m_ACC = m_acc;
        this.m_SUMM = m_summ;
    }

    public TblDatas(int mm_id, String mm_title, String mm_format, String mm_sighnature, String mm_key) {
        this.m_id = mm_id;
        this.m_title = mm_title;
        this.m_format = mm_format;
        this.m_sighnature = mm_sighnature;
        this.m_key = mm_key;
    }

    public void setM_PP(Integer mp_PP){
        this.m_PP = mp_PP;
    }
    public Integer getM_PP() {
        return m_PP;
    }

    public String getM_FIO() {
        return m_FIO;
    }
    public void setM_FIO(String mp_FIO) {
        this.m_FIO = mp_FIO;
    }

    public String getM_ACC() {
        return m_ACC;
    }
    public void setM_ACC(String mp_ACC) {
        this.m_ACC = mp_ACC;
    }

    public String getM_SUMM() {
        return m_SUMM;
    }
    public void setM_SUMM(String mp_SUMM) {
        this.m_SUMM = mp_SUMM;
    }


    public int getM_id() {
        return m_id;
    }
    public void setM_Id(int mm_id){
        m_id = mm_id;
    }

    public String getM_title(){
        return m_title;
    }
    public void setM_title(String mm_title){
        m_title = mm_title;
    }

    public String getM_format(){
        return m_format;
    }
    public void setM_format(String mm_format){
        m_format =  mm_format;
    }

    public String getM_sighnature(){
        return  m_sighnature;
    }
    public void setM_sighnature(String mm_sighnature){
        m_sighnature = mm_sighnature;
    }

    public String getM_key(){
        return m_key;
    }
    public void setM_key(String mm_key){
        m_key = mm_key;
    }
}
