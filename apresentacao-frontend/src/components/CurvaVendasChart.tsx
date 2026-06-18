import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts'
import type { PontoCurva } from '../services/dashboardService'

interface Props {
  pontos: PontoCurva[]
}

export default function CurvaVendasChart({ pontos }: Props) {
  if (pontos.length === 0) {
    return <p style={{ color: '#888', textAlign: 'center', padding: '2rem' }}>
      Ainda não há vendas para gerar a curva.
    </p>
  }
  const data = pontos.map(p => ({
    data: new Date(p.data).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' }),
    ingressos: Number(p.ingressos),
    receita: Number(p.receita),
  }))
  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="data" />
        <YAxis yAxisId="left" />
        <YAxis yAxisId="right" orientation="right" />
        <Tooltip />
        <Legend />
        <Line yAxisId="left" type="monotone" dataKey="ingressos" name="Ingressos" stroke="#7c6af7" strokeWidth={2} />
        <Line yAxisId="right" type="monotone" dataKey="receita" name="Receita (R$)" stroke="#059669" strokeWidth={2} />
      </LineChart>
    </ResponsiveContainer>
  )
}
