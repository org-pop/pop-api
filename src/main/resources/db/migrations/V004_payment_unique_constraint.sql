-- Garante no nível do banco a regra "um pagamento por pedido".
-- A entidade Payment já declarava @UniqueConstraint em order_id, mas com
-- ddl-auto=none essa anotação é ignorada. Sem a constraint, dois checkouts
-- simultâneos do mesmo pedido passavam pela checagem da aplicação e gravavam
-- dois pagamentos.
ALTER TABLE payment
    ADD CONSTRAINT uk_payment_order UNIQUE (order_id);

-- O índice não-único vira redundante: a UNIQUE constraint já cria seu próprio índice.
DROP INDEX IF EXISTS idx_payment_order_id;
