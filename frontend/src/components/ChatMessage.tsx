"use client";

import { ChatMessage } from "@/lib/types";
import JobList from "./JobList";

export default function ChatMessageBubble({ message }: { message: ChatMessage }) {
  const isUser = message.role === "user";

  return (
    <div className={`flex ${isUser ? "justify-end" : "justify-start"} mb-4`}>
      <div className={`max-w-[80%] ${isUser ? "order-1" : "order-1"}`}>
        {!isUser && (
          <div className="flex items-center gap-2 mb-1.5">
            <div className="w-6 h-6 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
              <svg className="w-3.5 h-3.5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09z" />
              </svg>
            </div>
            <span className="text-xs font-medium text-gray-400">Job Agent</span>
          </div>
        )}

        <div
          className={`rounded-2xl px-4 py-3 ${
            isUser
              ? "bg-blue-600 text-white rounded-br-md"
              : "bg-gray-800 text-gray-100 rounded-bl-md"
          }`}
        >
          <div className="text-sm whitespace-pre-wrap leading-relaxed">
            {message.content}
          </div>
        </div>

        {message.jobs && message.jobs.length > 0 && (
          <div className="mt-2">
            <JobList jobs={message.jobs} />
          </div>
        )}

        <p className={`text-[10px] text-gray-500 mt-1 ${isUser ? "text-right" : "text-left"}`}>
          {message.timestamp.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
        </p>
      </div>
    </div>
  );
}
