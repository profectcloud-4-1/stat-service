CREATE TABLE p_review (
      id UUID PRIMARY KEY,
      user_id UUID,
      product_id UUID,
      order_id UUID,
      rating integer,
      content VARCHAR(4096),
      created_at TIMESTAMP,
      updated_at TIMESTAMP,
      deleted_at TIMESTAMP
);