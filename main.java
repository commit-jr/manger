<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestão Financeira n8n</title>
    <style>
        :root {
            --primary: #4f46e5;
            --success: #22c55e;
            --danger: #ef4444;
            --bg: #f8fafc;
        }
        body { font-family: 'Segoe UI', sans-serif; background: var(--bg); display: flex; justify-content: center; padding: 20px; }
        .card { background: white; padding: 2rem; border-radius: 12px; shadow: 0 4px 6px rgba(0,0,0,0.1); width: 100%; max-width: 400px; border: 1px solid #e2e8f0; }
        h2 { color: #1e293b; text-align: center; margin-bottom: 1.5rem; }
        .group { margin-bottom: 1rem; }
        label { display: block; font-size: 0.875rem; font-weight: 600; margin-bottom: 0.5rem; color: #64748b; }
        input { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 6px; box-sizing: border-box; }
        button { width: 100%; padding: 0.8rem; margin-top: 1rem; border: none; border-radius: 6px; color: white; font-weight: bold; cursor: pointer; transition: opacity 0.2s; }
        .btn-save { background: var(--success); }
        .btn-del { background: var(--danger); }
        .btn-report { background: var(--primary); }
        button:hover { opacity: 0.9; }
        #output { margin-top: 1.5rem; padding: 1rem; border-radius: 6px; background: #f1f5f9; font-size: 0.9rem; color: #334155; display: none; white-space: pre-wrap; }
    </style>
</head>
<body>

<div class="card">
    <h2>💰 Meu Financeiro</h2>
    
    <div class="group">
        <label>Data</label>
        <input type="text" id="dia" placeholder="dd/mm/aaaa">
    </div>
    
    <div class="group">
        <label>Ganhos (R$)</label>
        <input type="number" id="ganhos" placeholder="0.00">
    </div>
    
    <div class="group">
        <label>Gastos (R$)</label>
        <input type="number" id="gastos" placeholder="0.00">
    </div>

    <button class="btn-save" onclick="enviar('finacas', true)">Salvar / Atualizar</button>
    <button class="btn-del" onclick="enviar('excluir', false)">Excluir Dia</button>
    <button class="btn-report" onclick="getRelatorio()">Ver Saldo Total</button>

    <div id="output"></div>
</div>

<script>
    // Preencher data atual automaticamente
    document.getElementById('dia').value = new Date().toLocaleDateString('pt-BR');

    async function enviar(path, fullData) {
        const out = document.getElementById('output');
        const body = { dia: document.getElementById('dia').value };
        
        if(fullData) {
            body.ganhos = document.getElementById('ganhos').value;
            body.gastos = document.getElementById('gastos').value;
        }

        out.style.display = 'block';
        out.innerText = "Processando...";

        try {
            const response = await fetch(`https://n8n.commitjr.com/webhook/${path}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            const resText = await response.text();
            out.innerText = "Resposta: " + resText;
        } catch (err) {
            out.innerText = "Erro ao conectar: " + err.message;
        }
    }

    async function getRelatorio() {
        const out = document.getElementById('output');
        out.style.display = 'block';
        out.innerText = "Buscando saldo...";

        try {
            const response = await fetch(`https://n8n.commitjr.com/webhook/relatorio`);
            const resText = await response.text();
            out.innerText = "Relatório:\n" + resText;
        } catch (err) {
            out.innerText = "Erro ao buscar: " + err.message;
        }
    }
</script>

</body>
</html>
