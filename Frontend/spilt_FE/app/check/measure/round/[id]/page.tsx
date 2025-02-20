// import RoundEvaluationClient from "./round-evaluation-client"

// // Generate static params for all possible round IDs (1-10)
// export function generateStaticParams() {
//   return Array.from({ length: 10 }, (_, i) => ({
//     id: (i + 1).toString(),
//   }))
// }

// export default function RoundEvaluationPage({ params }: { params: { id: string } }) {
//   return <RoundEvaluationClient params={params} />
// }



import RoundEvaluationClient from "./round-evaluation-client"

// Generate static params for all possible round IDs (1-10)
export function generateStaticParams() {
  return Array.from({ length: 10 }, (_, i) => ({
    id: (10 - i).toString(), // 10부터 1까지 역순으로 ID 생성
  }))
}

export default function RoundEvaluationPage({ params }: { params: { id: string } }) {
  // 받은 ID를 역순으로 변환
  const reversedId = (11 - parseInt(params.id)).toString()
  return <RoundEvaluationClient params={{ id: reversedId }} />
}