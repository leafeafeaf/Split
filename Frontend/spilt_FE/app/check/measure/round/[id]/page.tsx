import RoundEvaluationClient from "./round-evaluation-client"

// Generate static params for all possible round IDs (1-10)
export function generateStaticParams() {
  return Array.from({ length: 10 }, (_, i) => ({
    id: (i + 1).toString(),
  }))
}

export default function RoundEvaluationPage({ params }: { params: { id: string } }) {
  return <RoundEvaluationClient params={params} />
}

