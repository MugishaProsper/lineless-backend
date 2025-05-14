-- Reset the sequence for companies table
SELECT setval('companies_id_seq', 1, false);

-- Insert company with ID 1 if it doesn't exist
INSERT INTO companies (id, name, address, created_at, updated_at)
SELECT 1, 'Main Company', '1 Main St, Main City', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM companies WHERE id = 1); 