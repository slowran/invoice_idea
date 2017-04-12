package org.invoice.service;

import org.invoice.dao.InvoiceDao;
import org.invoice.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 李浩然 on 2017/4/12.
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private InvoiceDao invoiceDao;

    private Map<String, Invoice> outputInvoices;   // 出项发票列表
    private Map<String, Invoice> incomeInvoices;    // 进项发票列表

    @Override
    public List<Invoice> test(String invoiceId) {
        return invoiceDao.findInvoiceByInvoiceId(invoiceId);
    }
}