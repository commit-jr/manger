<!DOCTYPE html>
<html lang="pt-pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Painel Financeiro n8n</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f1f5f9;
        }
        .glass-card {
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .loading-spinner {
            border: 3px solid #f3f3f3;
            border-top: 3px solid #3b82f6;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            animation: spin 1s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body class="min-h-screen flex items-center justify-center p-4">

    <div class="max-w-md w-full glass-card p-8 rounded-3xl shadow-2xl">
        <div class="text-center mb-8">
            <h1 class="text-3xl font-bold text-gray-800 flex items-center justify-center gap-2">
                <i class="fa-solid fa-wallet text-blue-600"></i>
                Gestão n8n
            </h1>
            <p class="text-gray-500 mt-2">Controle de Ganhos e Gastos</p>
        </div>

        <!-- Formulário -->
        <div class="space-y-5">
            <div>
                <label class="block text-sm font-semibold text-gray-700 mb-1">Data</label>
                <input type="text" id="dia" 
                    class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                    placeholder="dd/mm/aaaa">
            </div>

            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1 text-green-600">Ganhos (R$)</label>
                    <input type="number" id="ganhos" 
                        class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-green-500 outline-none"
                        placeholder="0.00">
                </div>
                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1 text-red-600">Gastos (R$)</label>
                    <input type="number" id="gastos" 
                        class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-red-500 outline-none"
                        placeholder="0.00">
                </div>
            </div>

            <!-- Botões Principais -->
            <button onclick="enviar('finacas', true)" 
                id="btn-save"
                class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-xl shadow-lg transform active:scale-95 transition-all flex items-center justify-center gap-2">
                <i class="fa-solid fa-cloud-arrow-up"></i> Salvar / Atualizar
            </button>

            <div class="grid grid-cols-2 gap-4">
                <button onclick="enviar('excluir', false)" 
                    class="bg-white border-2 border-red-100 hover:border-red-500 text-red-500 font-bold py-3 rounded-xl transition-all flex items-center justify-center gap-2">
                    <i class="fa-solid fa-trash"></i> Eliminar
                </button>
                
                <button onclick="getRelatorio()" 
                    class="bg-gray-800 hover:bg-black text-white font-bold py-3 rounded-xl transition-all flex items-center justify-center gap-2 shadow-md">
                    <i class="fa-solid fa-chart-line"></i> Relatório
                </button>
            </div>
        </div>

        <!-- Area de Resposta -->
        <div id="status-container" class="mt-8 hidden">
            <div id="status-box" class="p-4 rounded-xl text-sm font-medium flex items-center gap-3">
                <!-- Conteúdo inserido via JS -->
            </div>
        </div>
    </div>

    <script>
        // Define a data atual automaticamente no formato da planilha
        document.addEventListener('DOMContentLoaded', () => {
            const hoje = new Date();
            const dia = String(hoje.getDate()).padStart(2, '0');
            const mes = String(hoje.getMonth() + 1).padStart(2, '0');
            const ano = hoje.getFullYear();
            document.getElementById('dia').value = `${dia}/${mes}/${ano}`;
        });

        const showStatus = (message, type) => {
            const container = document.getElementById('status-container');
            const box = document.getElementById('status-box');
            
            container.classList.remove('hidden');
            
            if (type === 'loading') {
                box.className = 'p-4 rounded-xl text-sm font-medium flex items-center gap-3 bg-blue-50 text-blue-700 border border-blue-100';
                box.innerHTML = '<div class="loading-spinner"></div> <span>A processar pedido...</span>';
            } else if (type === 'success') {
                box.className = 'p-4 rounded-xl text-sm font-medium flex items-center gap-3 bg-green-50 text-green-700 border border-green-100';
                box.innerHTML = '<i class="fa-solid fa-circle-check text-lg"></i> <span>' + message + '</span>';
            } else {
                box.className = 'p-4 rounded-xl text-sm font-medium flex items-center gap-3 bg-red-50 text-red-700 border border-red-100';
                box.innerHTML = '<i class="fa-solid fa-triangle-exclamation text-lg"></i> <span>' + message + '</span>';
            }
        };

        async function enviar(path, fullData) {
            const dia = document.getElementById('dia').value;
            const ganhos = document.getElementById('ganhos').value;
            const gastos = document.getElementById('gastos').value;

            if (!dia) return showStatus("Por favor, insira uma data.", "error");

            showStatus("", "loading");

            const payload = { dia };
            if (fullData) {
                payload.ganhos = ganhos || 0;
                payload.gastos = gastos || 0;
            }

            try {
                const response = await fetch(`https://n8n.commitjr.com/webhook/${path}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const result = await response.text();
                showStatus(result || "Operação concluída!", "success");
                
                // Limpar campos de valores após salvar
                if (fullData) {
                    document.getElementById('ganhos').value = '';
                    document.getElementById('gastos').value = '';
                }
            } catch (error) {
                showStatus("Erro na ligação: " + error.message, "error");
            }
        }

        async function getRelatorio() {
            showStatus("", "loading");

            try {
                const response = await fetch(`https://n8n.commitjr.com/webhook/relatorio`);
                const result = await response.text();
                showStatus(result, "success");
            } catch (error) {
                showStatus("Erro ao obter relatório: " + error.message, "error");
            }
        }
    </script>
</body>
</html>
